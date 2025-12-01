package com.loopers.domain.user;

import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceTest {

    @MockitoSpyBean
    private UserService userService;

    @MockitoSpyBean
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원가입 시")
    @Nested
    class Join {

        @DisplayName("User가 저장된다. (spy 검증)")
        @Test
        void returnsUser_whenValidValues() {
            //given
            String loginId = "test123";
            Gender gender = Gender.MALE;
            String birthDate = "2000-01-01";
            String email = "test123@gmail.com";
            UserCommand.Join joinCommand = new UserCommand.Join(
                    loginId,
                    gender,
                    birthDate,
                    email
            );

            //when
            userService.join(joinCommand);

            verify(userJpaRepository, times(1)).save(any(User.class));

            UserResult.GetUser savedUser = userService.getUserByLoginId(loginId);

            assertThat(savedUser.getLoginId()).isEqualTo(loginId);
            assertThat(savedUser.getGender()).isEqualTo(gender);
            assertThat(savedUser.getBirthDate()).isEqualTo(birthDate);
            assertThat(savedUser.getEmail()).isEqualTo(email);
        }

        @DisplayName("이미 가입된 ID로 회원가입 시도 시, CONFLICT 에러가 발생한다.")
        @Test
        void throwsException_whenJoinDuplicatedLoginId() {
            //given
            String loginId = "test123";

            UserCommand.Join joinCommand = new UserCommand.Join(
                    loginId,
                    Gender.MALE,
                    "2000-01-01",
                    "test@gmail.com"
            );

            UserCommand.Join joinCommandBySameId = new UserCommand.Join(
                    loginId,
                    Gender.FEMALE,
                    "2002-01-01",
                    "test2002@gmail.com"

            );

            //when
            userService.join(joinCommand);
            CoreException coreException = assertThrows(CoreException.class, () -> {
                userService.join(joinCommandBySameId);
            });


            //then
            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.CONFLICT);
            verify(userService, times(2)).join(any(UserCommand.Join.class));
        }

    }

    @DisplayName("사용자 조회 시")
    @Nested
    class Search {

        @DisplayName("사용자ID가 존재하지 않는 경우, NOT FOUND 에러가 발생한다")
        @ValueSource(
                longs = {
                        1L,
                        100L,
                        300L
                }
        )
        @ParameterizedTest
        void throwsException_whenInvalidUserId(Long userId) {
            //when
            CoreException coreException = assertThrows(CoreException.class, () -> {
                userService.getUser(userId);
            });

            //then
            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            verify(userService, times(1)).getUser(userId);
        }

        @DisplayName("로그인ID가 존재하지 않는 경우, NOT FOUND 에러가 발생한다.")
        @ValueSource(
                strings = {
                        "test",
                        "test123",
                        "test999"
                }
        )
        @ParameterizedTest
        void throwsException_whenInvalidLoginId(String loginId) {
            //when
            CoreException coreException = assertThrows(CoreException.class, () -> {
                userService.getUserByLoginId(loginId);
            });

            //then
            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            verify(userService, times(1)).getUserByLoginId(loginId);
        }

        @DisplayName("사용자ID가 존재하는 경우, User를 반환한다.")
        @Test
        void returnsUser_whenSavedUserById() {
            //given
            UserCommand.Join joinCommand = new UserCommand.Join(
                    "test",
                    Gender.FEMALE,
                    "2002-01-01",
                    "test@gmail.com"
            );
            UserResult.Join joinResult = userService.join(joinCommand);
            Long savedUserId = joinResult.getId();

            //when
            UserResult.GetUser getUserResult = userService.getUser(savedUserId);

            //then
            assertThat(getUserResult.getId()).isEqualTo(savedUserId);
            assertThat(getUserResult.getLoginId()).isEqualTo(joinResult.getLoginId());
            assertThat(getUserResult.getGender()).isEqualTo(joinResult.getGender());
            assertThat(getUserResult.getBirthDate()).isEqualTo(joinResult.getBirthDate());
            assertThat(getUserResult.getEmail()).isEqualTo(joinResult.getEmail());
            verify(userService, times(1)).join(any(UserCommand.Join.class));
            verify(userService, times(1)).getUser(savedUserId);
        }

        @DisplayName("로그인ID가 존재하는 경우, User를 반환한다.")
        @Test
        void returnsUser_whenSavedUserByLoginId() {
            //given
            String loginId = "test";
            UserCommand.Join joinCommand = new UserCommand.Join(
                    loginId,
                    Gender.MALE,
                    "2000-01-01",
                    "test@gmail.com"
            );
            UserResult.Join joinResult = userService.join(joinCommand);

            //when
            UserResult.GetUser getUserResult = userService.getUserByLoginId(loginId);

            //then
            assertThat(getUserResult.getId()).isEqualTo(joinResult.getId());
            assertThat(getUserResult.getLoginId()).isEqualTo(loginId);
            assertThat(getUserResult.getGender()).isEqualTo(joinResult.getGender());
            assertThat(getUserResult.getBirthDate()).isEqualTo(joinResult.getBirthDate());
            assertThat(getUserResult.getEmail()).isEqualTo(joinResult.getEmail());
            verify(userService, times(1)).join(any(UserCommand.Join.class));
            verify(userService, times(1)).getUserByLoginId(loginId);
        }

    }

}
