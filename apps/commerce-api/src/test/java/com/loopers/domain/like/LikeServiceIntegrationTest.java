package com.loopers.domain.like;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class LikeServiceIntegrationTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("좋아요 시")
    @Nested
    class Like {

        @DisplayName("동일한 상품에 대해 여러명이 좋아요/싫어요를 요청해도, 상품의 좋아요 개수가 정상 반영되어야 한다.")
        @Test
        void concurrency_test_for_like() throws InterruptedException {
            // given
            Brand brand = brandRepository.save(new Brand("Apple", "애플 주식회사(Apple Inc.)는 실리콘 밸리의 쿠퍼티노에 본사를 둔 미국의 다국적 기업이자 기술 회사이다."));
            Product product = productRepository.save(new Product("MacBook Pro M5", 2_390_000, brand.getId()));

            int userCount = 100;

            for (int i = 0; i < userCount; i++) {
                userRepository.save(User.builder()
                        .loginId("test" + i)
                        .email("test" + i + "@gmail.com")
                        .birthDate("2000-01-01")
                        .gender(Gender.MALE)
                        .build());
            }

            // when
            ExecutorService executorService = Executors.newFixedThreadPool(32);
            CountDownLatch latch = new CountDownLatch(userCount);

            for (int i = 0; i < userCount; i++) {
                long userId = i + 1;
                executorService.submit(() -> {
                    try {
                        likeService.like(new LikeCommand.Like(userId, product.getId()));
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            // then
            Product findProduct = productRepository.findById(product.getId()).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

            assertThat(likeRepository.countByProductId(product.getId())).isEqualTo(userCount);
            assertThat(findProduct.getLikeCount()).isEqualTo(userCount);

        }

    }

}
