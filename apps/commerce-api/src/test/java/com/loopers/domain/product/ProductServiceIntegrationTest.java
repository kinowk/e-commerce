package com.loopers.domain.product;

import com.loopers.infrastructure.product.StockJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockJpaRepository stockJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 재고 차감 시")
    @Nested
    class Deduct {
        @DisplayName("동시에 여러 주문이 요청되어도, 재고는 정상적으로 차감된다.")
        @Test
        void deductStock_concurrency() throws InterruptedException {
            // given
            Product product = Product.builder()
                    .brandId(1L)
                    .name("product")
                    .basePrice(1000)
                    .build();
            ProductOption productOption = ProductOption.builder()
                    .color("RED")
                    .size("M").extraPrice(0).quantity(100).build();
            product.getOptions().add(productOption);

            Product savedProduct = productRepository.save(product);
            Long productOptionId = savedProduct.getOptions().get(0).getId();
            Long stockId = savedProduct.getOptions().get(0).getStock().getId();

            int threadCount = 30;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            // when
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        ProductCommand.DeductStocks command = new ProductCommand.DeductStocks(List.of(
                                new ProductCommand.DeductStocks.Item(productOptionId, 1)
                        ));
                        productService.deductStocks(command);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            // then
            Stock finalStock = stockJpaRepository.findById(stockId).get();
            assertThat(finalStock.getQuantity()).isEqualTo(70);
        }

    }
}
