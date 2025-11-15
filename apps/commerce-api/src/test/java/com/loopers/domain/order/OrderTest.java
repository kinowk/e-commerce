package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTest {

    @DisplayName("주문 생성 시")
    @Nested
    class Create {

        @DisplayName("사용자 ID가 유효하지 않으면, BAD REQUEST 에러가 발생한다")
        @NullSource
        @ParameterizedTest
        void throwsException_whenInvalidUserId(Long userId) {
            //given
            Long totalPrice = 19000L;

            //when
            CoreException coreException = assertThrows(CoreException.class, () -> {
                Order.builder()
                        .userId(userId)
                        .totalPrice(totalPrice)
                        .build();
            });

            //then
            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("총 주문금액이 음수인 경우, BAD REQUEST 에러가 발생한다")
        @ValueSource(longs = {
                -1L, -100L, -1000L, Long.MIN_VALUE
        })
        @ParameterizedTest
        void throwsException_whenTotalPriceLessThanZero(Long totalPrice) {
            //given
            Long userId = 132L;

            //when
            CoreException coreException = assertThrows(CoreException.class, () -> {
                Order.builder()
                        .userId(userId)
                        .totalPrice(totalPrice)
                        .build();
            });

            //then
            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("유효한 값이면, 주문이 생성된다")
        @CsvSource(value = {
                "1|0",
                "99|23000",
                "1111|190000"
        }, delimiterString = "|")
        @ParameterizedTest
        void returnsOrder_whenValidValues(Long userId, Long totalPrice) {
            //given

            //when
            Order order = Order.builder()
                    .userId(userId)
                    .totalPrice(totalPrice)
                    .build();

            //then
            assertThat(order).isNotNull();
            assertThat(order.getUserId()).isEqualTo(userId);
            assertThat(order.getTotalPrice()).isEqualTo(totalPrice);
        }
    }
}
