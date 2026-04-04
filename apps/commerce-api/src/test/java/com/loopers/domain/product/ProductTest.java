package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.domain.product.attribute.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    @DisplayName("재고 차감 시")
    @Nested
    class DecreaseStock {

        @DisplayName("차감 수량이 0 이하이면, 400 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -100L, Long.MIN_VALUE})
        void throwsException_whenQuantityIsZeroOrLess(Long quantity) {
            // arrange
            Product product = new Product(1L, "상품명", "설명", 1000L, 10L);

            // act & assert
            assertThatThrownBy(() -> product.decreaseStock(quantity))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("차감 수량이 재고보다 크면, CONFLICT 에러가 발생한다.")
        @Test
        void throwsException_whenQuantityExceedsStock() {
            // arrange
            Product product = new Product(1L, "상품명", "설명", 1000L, 5L);

            // act & assert
            assertThatThrownBy(() -> product.decreaseStock(6L))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.CONFLICT);
        }

        @DisplayName("재고 차감 후 재고가 0이 되면, 상태가 SOLD_OUT으로 변경된다.")
        @Test
        void changesStatusToSoldOut_whenStockBecomesZero() {
            // arrange
            Product product = new Product(1L, "상품명", "설명", 1000L, 3L);

            // act
            product.decreaseStock(3L);

            // assert
            assertThat(product.getStock()).isEqualTo(0L);
            assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
        }

        @DisplayName("재고 차감 후 재고가 남아있으면, 상태가 ACTIVE를 유지한다.")
        @Test
        void remainsActive_whenStockIsRemaining() {
            // arrange
            Product product = new Product(1L, "상품명", "설명", 1000L, 5L);

            // act
            product.decreaseStock(2L);

            // assert
            assertThat(product.getStock()).isEqualTo(3L);
            assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        }
    }

    @DisplayName("좋아요 수 증가 시")
    @Nested
    class IncreaseLikeCount {

        @DisplayName("좋아요 수가 1 증가한다.")
        @Test
        void increasesLikeCountByOne() {
            // arrange
            Product product = new Product(1L, "상품명", "설명", 1000L, 10L);

            // act
            product.increaseLikeCount();

            // assert
            assertThat(product.getLikeCount()).isEqualTo(1L);
        }
    }

    @DisplayName("좋아요 수 감소 시")
    @Nested
    class DecreaseLikeCount {

        @DisplayName("좋아요 수가 1 감소한다.")
        @Test
        void decreasesLikeCountByOne() {
            // arrange
            Product product = new Product(1L, "상품명", "설명", 1000L, 10L);
            product.increaseLikeCount();

            // act
            product.decreaseLikeCount();

            // assert
            assertThat(product.getLikeCount()).isEqualTo(0L);
        }

        @DisplayName("좋아요 수가 0이면, 감소하지 않는다.")
        @Test
        void doesNotDecrease_whenLikeCountIsZero() {
            // arrange
            Product product = new Product(1L, "상품명", "설명", 1000L, 10L);

            // act
            product.decreaseLikeCount();

            // assert
            assertThat(product.getLikeCount()).isEqualTo(0L);
        }
    }
}
