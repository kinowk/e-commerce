package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserOutput;
import com.loopers.domain.user.Gender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    public record Join(Long id, String loginId, Gender gender, String birthDate, String email) {
        public static Join from(UserOutput.Join output) {
            return new Join(
                    output.id(),
                    output.loginId(),
                    output.gender(),
                    output.birthDate(),
                    output.email()
            );
        }
    }

    public record GetUser(Long id, String loginId, Gender gender, String birthDate, String email) {
        public static GetUser from(UserOutput.GetUser output) {
            return new GetUser(
                    output.id(),
                    output.loginId(),
                    output.gender(),
                    output.birthDate(),
                    output.email()
            );
        }
    }
}
