package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserOutput;
import com.loopers.domain.user.attribute.Gender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    public record Join(Long id, String username, String loginId, String email, String birthDate, Gender gender) {
        public static Join from(UserOutput.Join output) {
            return new Join(
                    output.id(),
                    output.username(),
                    output.loginId(),
                    output.email(),
                    output.birthDate(),
                    output.gender()
            );
        }
    }

    public record GetUser(Long id, String username, String loginId, String email, String birthDate, Gender gender) {
        public static GetUser from(UserOutput.GetUser output) {
            return new GetUser(
                    output.id(),
                    output.username(),
                    output.loginId(),
                    output.email(),
                    output.birthDate(),
                    output.gender()
            );
        }
    }
}
