package com.loopers.domain.user;

import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserInput;
import com.loopers.application.user.UserOutput;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.user.UserRequest;
import com.loopers.interfaces.api.user.UserResponse;
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
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserFacade userFacade;

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

        @DisplayName("회원가입 시 User 저장이 수행된다. (spy 검증)")
        @Test
        void returnsUser_whenJoin() {
            String loginId = "kinowk123";
            Gender gender = Gender.MALE;
            String birthDate = "2000-01-01";
            String email = "test@gmail.com";
            UserRequest.Join request = new UserRequest.Join(
                    loginId,
                    gender,
                    birthDate,
                    email
            );

            userFacade.join(request.toInput());

            verify(userJpaRepository, times(1)).save(any(User.class));

            Optional<UserResult.GetUser> result = userService.findUserByLoginId(loginId);

            assertThat(result).isPresent();
            assertAll(
                    () -> assertThat(result.get().getLoginId()).isEqualTo(loginId),
                    () -> assertThat(result.get().getGender()).isEqualTo(gender),
                    () -> assertThat(result.get().getBirthDate()).isEqualTo(birthDate),
                    () -> assertThat(result.get().getEmail()).isEqualTo(email)
            );
        }

        @DisplayName("이미 가입된 ID로 회원가입 시도 시, 실패한다.")
        @Test
        void throwsException_whenDuplicatedLoingId() {
            String loginId = "kinowk";
            UserRequest.Join request = new UserRequest.Join(
                    loginId,
                    Gender.MALE,
                    "2000-01-01",
                    "kinowk1@gmail.com"
            );

            UserRequest.Join requestSameLoginId = new UserRequest.Join(
                    loginId,
                    Gender.FEMAIL,
                    "2000-02-02",
                    "kinowk2@gmail.com"
            );

            userFacade.join(request.toInput());

            CoreException coreException = assertThrows(CoreException.class, () -> {
                userFacade.join(requestSameLoginId.toInput());
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.CONFLICT);

            verify(userService, times(2)).join(any(UserCommand.Join.class));
        }

    }

    @DisplayName("회원조회 시")
    @Nested
    class Search {
        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void returnsUserResult_whenJoinedLoginId() {
            //given
            String loginId = "kinowk1";
            Gender gender = Gender.MALE;
            String birthDate = "2000-01-31";
            String email = "abc123@gmail.com";
            UserInput.Join input = new UserInput.Join(loginId, gender, birthDate, email);

            userFacade.join(input);

            //when
            UserOutput.GetUser getUserOutput = userFacade.getUserByLoginId(loginId);

            //then
            UserResponse.GetUser getUserResponse = UserResponse.GetUser.from(getUserOutput);
            assertAll(
                    () -> assertThat(getUserResponse).isNotNull(),
                    () -> assertThat(getUserResponse.loginId()).isEqualTo(loginId),
                    () -> assertThat(getUserResponse.gender()).isEqualTo(gender),
                    () -> assertThat(getUserResponse.birthDate()).isEqualTo(birthDate),
                    () -> assertThat(getUserResponse.email()).isEqualTo(email)
            );
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void returnsNull_whenNonExistsLoginId() {
            //given
            String loginId = "kinowk123";

            //when
            Optional<UserResult.GetUser> result = userService.findUserByLoginId(loginId);

            //then
            assertThat(result.isEmpty()).isTrue();
        }

    }

}
