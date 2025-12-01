package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointTest {

    @DisplayName("포인트 생성 시")
    @Nested
    class Create {
        @DisplayName("포인트 잔액이 0보다 작은 경우, Point 객체 생성에 실패한다")
        @ParameterizedTest
        @ValueSource(longs = {
                -1L, -10L, -100L, Long.MIN_VALUE
        })
        void throwsException_whenBalanceIsLessZero(Long balance) {
            //given
            Long userId = 1L;

            //when & then
            CoreException coreException = assertThrows(CoreException.class, () -> {
                Point.builder()
                        .userId(userId)
                        .balance(balance)
                        .build();
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("유효한 값이면, Point 객체를 생성한다.")
        @CsvSource(value = {
                "0|0",
                "1|1",
                "100|100",
                "123456|100000000",
        }, delimiterString = "|"
        )
        @ParameterizedTest
        void returnsPoint_whenValidValues(Long userId, Long balance) {
            //when
            Point point = Point.builder()
                    .userId(userId)
                    .balance(balance)
                    .build();

            //then
            assertThat(point.getUserId()).isEqualTo(userId);
            assertThat(point.getBalance()).isEqualTo(balance);
        }
    }

    @DisplayName("포인트 충전 시")
    @Nested
    class Charge {
        @DisplayName("충전 금액이 0이하인 경우, BAD REQUEST 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(longs = {
                0, -1L, -999L, Long.MIN_VALUE
        })
        void throwsException_whenChargePointLessThanEqualZero(Long amount) {
            //given
            Point point = Point.builder()
                    .userId(1L)
                    .balance(0L)
                    .build();

            //when & than
            CoreException coreException = assertThrows(CoreException.class, () -> {
                point.charge(amount);
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    @DisplayName("포인트 사용 시")
    @Nested
    class Use {
        @DisplayName("사용 금액이 0이하인 경우, BAD REQUEST 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(longs = {
                -1L, -9999L, Long.MIN_VALUE
        })
        void throwsException_whenUsePointLessThanZero(Long amount) {
            //given
            Point point = Point.builder()
                    .userId(1L)
                    .balance(0L)
                    .build();

            //when & then
            CoreException coreException = assertThrows(CoreException.class, () -> {
                point.use(amount);
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

}
