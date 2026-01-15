package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    @DisplayName("포인트 객체 생성 시")
    @Nested
    class Create {

        @DisplayName("잔액이 null인 경우, 400 에러를 발생한다.")
        @Test
        void throwsException_whenAmountIsNull() {
            // given
            Long userId = 1L;
            Long balance = null;

            // when & then
            assertThatThrownBy(() -> new Point(userId, balance))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("잔액이 0보다 작은 경우, 400 에러를 발생한다.")
        @ParameterizedTest
        @ValueSource(longs = {
                -1L,
                -100L,
                -10000L,
                Long.MIN_VALUE
        })
        void throwsException_whenAmountIsLessThanZero(Long balance) {
            // given
            Long userId = 1L;

            // when & then
            assertThatThrownBy(() -> new Point(userId, balance))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

    }

    @DisplayName("포인트 충전 시")
    @Nested
    class Charge {

        @DisplayName("충전 금액이 null인 경우, 400 에러를 발생한다")
        @Test
        void throwsException_whenChargeAmountIsNull() {
            // given
            Long userId = 1L;
            Long balance = null;

            // when & then
            assertThatThrownBy(() -> new Point(userId, balance))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("충전 금액이 0보다 작은 경우, 400 에러를 발생한다")
        @ParameterizedTest
        @ValueSource(longs = {
                -1L,
                -100L,
                -10000L,
                Long.MIN_VALUE
        })
        void throwsException_whenChargeAmountIsLessThanZero(Long balance) {
            // given
            Long userId = 1L;

            // when & then
            assertThatThrownBy(() -> new Point(userId, balance))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트 차감 시")
    @Nested
    class Deduct {

        @DisplayName("차감 금액이 null인 경우, 400 에러를 발생한다.")
        @Test
        void throwsException_whenDeductAmountIsNull() {
            // given
            Point point = new Point(1L, 1000L);
            Long amount = null;

            // when & then
            assertThatThrownBy(() -> point.deduct(amount))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("차감 금액이 0보다 작은 경우, 400 에러를 발생한다")
        @ParameterizedTest
        @ValueSource(longs = {
                -1L,
                -100L,
                -10000L,
                Long.MIN_VALUE
        })
        void throwsException_whenDeductAmountIsLessThanZero(Long amount) {
            // given
            Point point = new Point(1L, 1000L);

            // when & then
            assertThatThrownBy(() -> point.deduct(amount))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("차감 금액이 잔액보다 큰 경우, 400 에러가 발생한다.")
        @Test
        void throwsException_whenDeductAmountIsGreaterThanBalance() {
            // given
            Point point = new Point(1L, 1000L);
            Long amount = 1001L;

            // when & then
            assertThatThrownBy(() -> point.deduct(amount))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

    }
}
