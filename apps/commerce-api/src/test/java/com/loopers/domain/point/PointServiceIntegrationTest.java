package com.loopers.domain.point;

import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserInput;
import com.loopers.application.user.UserOutput;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserCommand;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
class PointServiceIntegrationTest {

    @Autowired
    private UserFacade userFacade;

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

    @DisplayName("포인트 사용 시")
    @Nested
    class Use {
        @DisplayName("동일한 유저가 서로 다른 주문을 동시에 수행해도, 포인트가 정상적으로 차감되어야 한다.")
        @Test
        void usePoint_concurrency() throws InterruptedException {
            UserCommand.Join joinCommand = new UserCommand.Join("test", Gender.MALE, "2000-01-01", "test@gmail.com");
            UserInput.Join joinInput = new UserInput.Join("test", Gender.MALE, "2000-01-01", "test@gmail.com");
            UserOutput.Join joinOutput = userFacade.join(joinInput);
            Long userId = joinOutput.id();

            long chargeAmount = 1_000_000L;
            PointCommand.Charge chargeCommand = new PointCommand.Charge(userId, chargeAmount);
            pointService.charge(chargeCommand);

            int threadCount = 30;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            long useAmount = 10_000L;
            PointCommand.Use useCommand = new PointCommand.Use(userId, useAmount);

            //when
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        pointService.use(useCommand);
                    } catch (Exception e) {
                        log.error("error", e);
                    } finally {
                        latch.countDown();
                    }

                });
            }

            latch.await();

            //then
            PointResult.GetPoint point = pointService.getPoint(userId);
            assertThat(point.getBalance()).isEqualTo(chargeAmount - (useAmount * threadCount));
        }
    }
}
