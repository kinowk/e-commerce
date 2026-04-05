package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.domain.order.attribute.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @DisplayName("주문 객체 생성 시")
    @Nested
    class CreateOrder {

        @DisplayName("사용자 ID가 null이면, 400 에러가 발생한다.")
        @Test
        void throwsException_whenUserIdIsNull() {
            // act & assert
            assertThatThrownBy(() -> new Order(null, 1000L, 0L, null))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("주문 금액이 null이면, 400 에러가 발생한다.")
        @Test
        void throwsException_whenTotalAmountIsNull() {
            // act & assert
            assertThatThrownBy(() -> new Order(1L, null, 0L, null))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("주문 금액이 0보다 작으면, 400 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(longs = {-1L, -100L, Long.MIN_VALUE})
        void throwsException_whenTotalAmountIsNegative(Long totalAmount) {
            // act & assert
            assertThatThrownBy(() -> new Order(1L, totalAmount, 0L, null))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("주문 생성 시 상태가 PENDING으로 설정된다.")
        @Test
        void setsStatusToPending_whenOrderIsCreated() {
            // act
            Order order = new Order(1L, 5000L, 0L, null);

            // assert
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        }
    }

    @DisplayName("주문 항목 객체 생성 시")
    @Nested
    class CreateOrderItem {

        @DisplayName("수량이 0 이하이면, 400 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -100L, Long.MIN_VALUE})
        void throwsException_whenQuantityIsZeroOrLess(Long quantity) {
            // act & assert
            assertThatThrownBy(() -> new OrderItem(1L, 1L, quantity, 1000L))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("단가가 0보다 작으면, 400 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(longs = {-1L, -100L, Long.MIN_VALUE})
        void throwsException_whenUnitPriceIsNegative(Long unitPrice) {
            // act & assert
            assertThatThrownBy(() -> new OrderItem(1L, 1L, 2L, unitPrice))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("총 가격이 수량 * 단가로 계산된다.")
        @Test
        void calculatesTotalPrice_asQuantityTimesUnitPrice() {
            // arrange
            Long quantity = 3L;
            Long unitPrice = 2000L;

            // act
            OrderItem item = new OrderItem(1L, 1L, quantity, unitPrice);

            // assert
            assertThat(item.getTotalPrice()).isEqualTo(quantity * unitPrice);
        }
    }
}
