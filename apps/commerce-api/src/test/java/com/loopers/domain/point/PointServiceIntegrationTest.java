package com.loopers.domain.point;

import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.attribute.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트 조회 시")
    @Nested
    class GetPoint {

        @DisplayName("해당 ID의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnsPoint_whenUserExists() {
            // given
            UserCommand.Join joinCommand = new UserCommand.Join(
                    "testuser", "test123", "password123", "test@example.com", "1990-01-01", Gender.MALE
            );
            userService.join(joinCommand);

            // when
            PointResult.GetPoint result = pointService.getPoint("test123");

            // then
            assertThat(result.loginId()).isEqualTo("test123");
            assertThat(result.balance()).isEqualTo(0L);
        }

        @DisplayName("해당 ID의 회원이 존재하지 않을 경우, NOT_FOUND 에러가 발생한다.")
        @Test
        void throwsException_whenUserNotFound() {
            // given
            String nonExistLoginId = "nonexist123";

            // when & then
            assertThatThrownBy(() -> pointService.getPoint(nonExistLoginId))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @DisplayName("포인트 충전 시")
    @Nested
    class Charge {

        @DisplayName("존재하지 않는 유저 ID로 충전을 시도한 경우, 실패한다.")
        @Test
        void throwsException_whenUserNotFound() {
            // given
            PointCommand.Charge command = new PointCommand.Charge("nonexist123", 1000L);

            // when & then
            assertThatThrownBy(() -> pointService.charge(command))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
