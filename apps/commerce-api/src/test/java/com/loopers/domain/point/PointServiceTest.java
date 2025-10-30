package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@MockitoSettings
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @DisplayName("포인트 충전 시")
    @Nested
    class Charge {

        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @ParameterizedTest
        @ValueSource(longs = {
                0, -1, -10, -100, -1000, Long.MIN_VALUE
        })
        void throwException_whenAmountIsLessThanEqualZero(Long amount) {
            //given
            Long userId = 1L;

            PointCommand.Charge command = new PointCommand.Charge(userId, amount);

            given(pointRepository.findByUserId(userId)).willReturn(
                    Optional.of(Point.builder()
                            .userId(userId)
                            .balance(0L)
                            .build()
                    )
            );

            //when & then
            CoreException coreException = assertThrows(CoreException.class, () -> {
                pointService.charge(command);
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

    }
}
