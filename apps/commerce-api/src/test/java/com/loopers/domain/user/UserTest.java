package com.loopers.domain.user;

import com.loopers.domain.user.attribute.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class UserTest {


    @DisplayName("사용자 객체 생성 시")
    @Nested
    class Create {

        @DisplayName("ID가 영문 및 숫자 10자 이내 형식에 맞지 않으면, 400 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "test_123",
                "test1234567",
                "abcdefghijk",
                "01234567890"
        })
        void throwsException_whenInvalidLoginId(String loginId) {
            // given
            String username = "사용자";
            String password = "password123";
            String email = "test@gmail.com";
            String birthDate = "1994-04-07";

            // when & then
            assertThatThrownBy(() -> new User(username, loginId, password, email, birthDate, Gender.MALE))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, 400 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "invalid-email",
                "test@",
                "test@domain",
                "test@domain.",
                "@domain.com"
        })
        void throwsException_whenInvalidEmail(String email) {
            // given
            String username = "사용자";
            String loginId = "testuser";
            String password = "password123";
            String birthDate = "1994-04-07";

            // when & then
            assertThatThrownBy(() -> new User(username, loginId, password, email, birthDate, Gender.MALE))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, 400 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "19940407",
                "94-04-07",
                "1994/04/07",
                "1994-4-7",
                "invalid-date"
        })
        void throwsException_whenInvalidBirthDate(String birthDate) {
            // given
            String username = "사용자";
            String loginId = "testuser";
            String password = "password123";
            String email = "test@gmail.com";

            // when & then
            assertThatThrownBy(() -> new User(username, loginId, password, email, birthDate, Gender.MALE))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("Gender가 null인 경우, 400 에러가 발생한다")
        @Test
        void throwsException_whenInvalidGender() {
            // given
            String username = "사용자";
            String loginId = "testuser";
            String password = "password123";
            String email = "test@gmail.com";
            String birthDate = "1994-04-07";

            // when & then
            assertThatThrownBy(() -> new User(username, loginId, password, email, birthDate, null))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }
    }


}
