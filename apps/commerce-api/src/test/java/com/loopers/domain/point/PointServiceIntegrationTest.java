package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트 충전 시")
    @Nested
    class Charge {

        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
        @Test
        void throwException_whenChargeInvalidUser() {
            //given
            Long userId = 112L;
            Long amount = 10_000L;

            PointCommand.Charge command = new PointCommand.Charge(userId, amount);

            //when & then
            CoreException coreException = assertThrows(CoreException.class, () -> {
                pointService.charge(command);
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("포인트 조회 시")
    @Nested
    class Search {

        @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnsPoint_whenJoinUser() {
            //given
            Long userId = 1L;
            Long balance = 12_345L;
            Point point = Point.builder()
                    .userId(userId)
                    .balance(balance)
                    .build();

            pointRepository.save(point);

            //when
            PointResult.GetPoint result = pointService.getPoint(userId);

            //then
            assertAll(
                    () -> assertThat(result.getUserId()).isEqualTo(userId),
                    () -> assertThat(result.getBalance()).isEqualTo(balance)
            );
        }
    }
}
