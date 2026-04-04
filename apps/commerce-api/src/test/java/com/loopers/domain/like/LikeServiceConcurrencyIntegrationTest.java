package com.loopers.domain.like;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.attribute.Gender;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LikeServiceConcurrencyIntegrationTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandJpaRepository brandJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("동일한 상품에 대해 여러명이 좋아요를 동시에 요청해도, 상품의 좋아요 개수가 정상 반영되어야 한다.")
    @Test
    void likeCountShouldBeProperlyReflectedWhenMultipleUsersLikeConcurrently() throws InterruptedException {
        // arrange
        Brand brand = brandJpaRepository.save(new Brand("테스트브랜드", "설명"));
        Product product = productRepository.save(new Product(brand.getId(), "좋아요상품", "설명", 1000L, 100L));

        int threadCount = 10;
        for (int i = 0; i < threadCount; i++) {
            userService.join(new UserCommand.Join(
                    "lkUser" + i, "lkUser" + i, "password123",
                    "test@test.com", "1990-01-01", Gender.MALE
            ));
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        // act
        for (int i = 0; i < threadCount; i++) {
            final String userLoginId = "lkUser" + i;
            executor.submit(() -> {
                try {
                    likeService.addLike(new LikeCommand.Toggle(userLoginId, product.getId()));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 동시성 이슈로 실패한 경우
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // assert
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getLikeCount()).isEqualTo(successCount.get());
        assertThat(updatedProduct.getLikeCount()).isGreaterThan(0);
    }
}
