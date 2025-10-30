package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointTest {

    @DisplayName("포인트 잔액이 0보다 작은 경우, Point 객체 생성에 실패한다")
    @ParameterizedTest
    @ValueSource(longs = {
            -1L, -10L, -100L, Long.MIN_VALUE
    })
    void throwsExceptons_whenBalanceIsLessZero(Long balance) {
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

    @DisplayName("포인트 충전 시 충전 금액이 0이하인 경우, Bad Request 에러가 발생한다.")
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
