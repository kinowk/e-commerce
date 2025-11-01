package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @DisplayName("회원가입 시")
    @Nested
    class Join {

        @DisplayName("ID가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                " ",
                "loginIdOver10",
                "_underscore"
        })
        void throwsException_whenInvalidLoginId(String loginId) {
            //given
            Gender gender = Gender.MALE;
            String birthDate = "2000-01-01";
            String email = "test@gmail.com";

            //when & then
            CoreException coreException = assertThrows(CoreException.class, () -> {
                User.builder()
                        .loginId(loginId)
                        .gender(gender)
                        .birthDate(birthDate)
                        .email(email)
                        .build();
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                " ",
                "abc",
                "abc@",
                "abc@gmail",
                "abc@gmail."
        })
        void throwsException_whenInvalidEmail(String email) {
            //given
            String loginId = "kinowk123";
            Gender gender = Gender.FEMALE;
            String birthDate = "2000-01-01";

            //when & then
            CoreException coreException = assertThrows(CoreException.class, () -> {
                User.builder()
                        .loginId(loginId)
                        .gender(gender)
                        .birthDate(birthDate)
                        .email(email)
                        .build();
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "1995/10/26",   // 구분자
                "26-10-1995",   // 순서
                "19951026",     // 구분자 없음
                "2024-13-01",   // 잘못된 월
                "2024-02-30",   // 잘못된 일
                "invalid-date"  // 유효하지 않은 문자열
        })
        void throwsException_whenInvalidBirthDate(String birthDate) {
            //given
            String loginId = "kinowk321";
            Gender gender = Gender.MALE;
            String email = "test@gmail.com";

            //when & then
            CoreException coreException = assertThrows(CoreException.class, () -> {
                User.builder()
                        .loginId(loginId)
                        .gender(gender)
                        .birthDate(birthDate)
                        .email(email)
                        .build();
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

}
