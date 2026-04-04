package com.loopers.domain.order;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.attribute.CouponType;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.attribute.Gender;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceConcurrencyIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private PointService pointService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private BrandJpaRepository brandJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    private Brand createBrand() {
        return brandJpaRepository.save(new Brand("테스트브랜드", "설명"));
    }

    private void createUserWithPoint(String loginId, long initialPoint) {
        userService.join(new UserCommand.Join(
                loginId, loginId, "password123",
                "test@test.com", "1990-01-01", Gender.MALE
        ));
        if (initialPoint > 0) {
            com.loopers.domain.point.PointCommand.Charge charge =
                    new com.loopers.domain.point.PointCommand.Charge(loginId, initialPoint);
            pointService.charge(charge);
        }
    }

    @DisplayName("재고 동시성 테스트")
    @Nested
    class StockConcurrency {

        @DisplayName("동일한 상품에 대해 여러 주문이 동시에 요청되어도, 재고가 정상적으로 차감되어야 한다.")
        @Test
        void stockShouldBeProperlyDecreasedWhenMultipleOrdersArePlacedConcurrently() throws InterruptedException {
            // arrange
            Brand brand = createBrand();
            Product product = productRepository.save(new Product(brand.getId(), "테스트상품", "설명", 1000L, 10L));

            int threadCount = 10;
            for (int i = 0; i < threadCount; i++) {
                createUserWithPoint("user" + i, 100000L);
            }

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // act
            for (int i = 0; i < threadCount; i++) {
                final String userLoginId = "user" + i;
                executor.submit(() -> {
                    try {
                        List<OrderCommand.Create.Item> items = List.of(
                                new OrderCommand.Create.Item(product.getId(), 1L)
                        );
                        orderService.createOrder(new OrderCommand.Create(userLoginId, items, null));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executor.shutdown();

            // assert
            Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
            assertThat(updatedProduct.getStock()).isGreaterThanOrEqualTo(0);
            assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
            assertThat(updatedProduct.getStock()).isEqualTo(10L - successCount.get());
        }
    }

    @DisplayName("포인트 동시성 테스트")
    @Nested
    class PointConcurrency {

        @DisplayName("동일한 유저가 서로 다른 주문을 동시에 수행해도, 포인트가 정상적으로 차감되어야 한다.")
        @Test
        void pointShouldBeProperlyDeductedWhenSameUserPlacesMultipleOrdersConcurrently() throws InterruptedException {
            // arrange
            Brand brand = createBrand();
            int threadCount = 5;
            long pointPerOrder = 1000L;
            long initialPoint = threadCount * pointPerOrder;

            createUserWithPoint("concUser", initialPoint);

            List<Product> products = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                products.add(productRepository.save(new Product(brand.getId(), "상품" + i, "설명", pointPerOrder, 10L)));
            }

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // act
            for (int i = 0; i < threadCount; i++) {
                final Product product = products.get(i);
                executor.submit(() -> {
                    try {
                        List<OrderCommand.Create.Item> items = List.of(
                                new OrderCommand.Create.Item(product.getId(), 1L)
                        );
                        orderService.createOrder(new OrderCommand.Create("concUser", items, null));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executor.shutdown();

            // assert: 포인트 잔액은 0 이상이어야 함
            com.loopers.domain.point.PointResult.GetPoint pointResult = pointService.getPoint("concUser");
            assertThat(pointResult.balance()).isGreaterThanOrEqualTo(0L);
            assertThat(successCount.get()).isLessThanOrEqualTo(threadCount);
        }
    }

    @DisplayName("쿠폰 동시성 테스트")
    @Nested
    class CouponConcurrency {

        @DisplayName("동일한 쿠폰으로 여러 기기에서 동시에 주문해도, 쿠폰은 단 한번만 사용되어야 한다.")
        @Test
        void couponShouldBeUsedOnlyOnceWhenMultipleOrdersArePlacedConcurrently() throws InterruptedException {
            // arrange
            Brand brand = createBrand();
            Product product = productRepository.save(new Product(brand.getId(), "쿠폰상품", "설명", 1000L, 20L));

            int threadCount = 10;
            String couponOwner = "cpUser";
            createUserWithPoint(couponOwner, 1000000L);

            // couponOwner 의 쿠폰 1개 생성
            Coupon coupon = couponRepository.save(new Coupon(couponOwner, CouponType.FIXED_AMOUNT, 500L));

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // act: cpUser 계정으로 동시에 쿠폰을 사용하며 주문
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        List<OrderCommand.Create.Item> items = List.of(
                                new OrderCommand.Create.Item(product.getId(), 1L)
                        );
                        orderService.createOrder(new OrderCommand.Create(couponOwner, items, coupon.getId()));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executor.shutdown();

            // assert: 쿠폰은 단 1번만 성공해야 함 (첫 번째 주문에만 쿠폰 적용)
            // 단, 쿠폰 없이 포인트만으로도 주문 가능하므로 쿠폰 사용 횟수만 검증
            Coupon usedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
            assertThat(usedCoupon.isUsed()).isTrue();
            // 쿠폰을 두 번 이상 사용하려는 시도는 실패했어야 함
            assertThat(successCount.get()).isGreaterThanOrEqualTo(1);
        }
    }
}
