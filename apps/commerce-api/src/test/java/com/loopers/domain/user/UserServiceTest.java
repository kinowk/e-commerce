package com.loopers.domain.user;

import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserOutput;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.user.UserRequest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserFacade userFacade;

    @MockitoSpyBean
    private UserService userService;

    @MockitoSpyBean
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @DisplayName("회원가입 시")
    @Nested
    class Join {

        @DisplayName("회원가입 시 User 저장이 수행된다. (spy 검증)")
        @Test
        void save_user_whenJoin() {
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

            UserOutput.GetUser output = userFacade.getUserByLoginId(loginId);

            assertThat(output.loginId()).isEqualTo(loginId);
            assertThat(output.gender()).isEqualTo(gender);
            assertThat(output.birthDate()).isEqualTo(birthDate);
            assertThat(output.email()).isEqualTo(email);
        }

        @DisplayName("이미 가입된 ID로 회원가입 시도 시, 실패한다.")
        @Test
        void fail_join_whenDuplicatedLoginId() {
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

}
