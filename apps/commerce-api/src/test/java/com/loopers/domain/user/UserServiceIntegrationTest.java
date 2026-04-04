package com.loopers.domain.user;

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
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @MockitoSpyBean
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원가입 시")
    @Nested
    class Join {

        @DisplayName("회원가입 시 User가 저장된다.")
        @Test
        void saveUser_whenJoin() {
            // given
            UserCommand.Join command = new UserCommand.Join(
                    "testuser",
                    "test123",
                    "password123",
                    "test@example.com",
                    "1990-01-01",
                    Gender.MALE
            );

            // when
            userService.join(command);

            //then
            verify(userRepository, times(1)).save(any(User.class));
        }

        @DisplayName("회원가입 시 로그인ID가 중복된 경우, CONFLICT 에러가 발생한다")
        @Test
        void throwsException_whenLoginIdIsDuplicated() {
            // given
            String loginId = "test123";
            UserCommand.Join firstCommand = new UserCommand.Join(
                    "testuser1",
                    loginId,
                    "password123",
                    "test1@example.com",
                    "1990-01-01",
                    Gender.MALE
            );
            UserCommand.Join secondCommand = new UserCommand.Join(
                    "testuser2",
                    loginId,
                    "password456",
                    "test2@example.com",
                    "1990-02-02",
                    Gender.FEMALE
            );

            // when
            userService.join(firstCommand);

            // then
            assertThatThrownBy(() -> userService.join(secondCommand))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("이미 존재하는 리소스입니다.")
                    .extracting("errorType")
                    .isEqualTo(ErrorType.CONFLICT);
        }
    }

    @DisplayName("회원조회 시")
    @Nested
    class Search {

        @DisplayName("회원이 존재하는 경우, 회원 정보가 반환된다.")
        @Test
        void returnsUser_whenExistUser() {
            // given
            UserCommand.Join joinCommand = new UserCommand.Join(
                    "testuser",
                    "test123",
                    "password123",
                    "test@example.com",
                    "1990-01-01",
                    Gender.MALE
            );
            userService.join(joinCommand);

            // when
            UserResult.GetUser result = userService.getUser("test123");

            // then
            assertThat(result.username()).isEqualTo("testuser");
            assertThat(result.loginId()).isEqualTo("test123");
            assertThat(result.email()).isEqualTo("test@example.com");
            assertThat(result.birthDate()).isEqualTo("1990-01-01");
            assertThat(result.gender()).isEqualTo(Gender.MALE);
            assertThat(result.id()).isNotNull();
        }

        @DisplayName("회원이 존재하지 않는 경우, 404 에러가 발생한다.")
        @Test
        void throwsException_whenNonExistUser() {
            // given
            String nonExistLoginId = "nonexist123";

            // when & then
            assertThatThrownBy(() -> userService.getUser(nonExistLoginId))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("존재하지 않는 요청입니다.")
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
