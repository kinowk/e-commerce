package com.loopers.application.user;

import com.loopers.domain.user.UserResult;
import com.loopers.domain.user.attribute.Gender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserOutput {

    public record Join(Long id, String username, String loginId, String email, String birthDate, Gender gender) {
        public static Join from(UserResult.Join result) {
            return new Join(
                    result.id(),
                    result.username(),
                    result.loginId(),
                    result.email(),
                    result.birthDate(),
                    result.gender()
            );
        }
    }

    public record GetUser(Long id, String username, String loginId, String email, String birthDate, Gender gender) {
        public static GetUser from(UserResult.GetUser result) {
            return new GetUser(
                    result.id(),
                    result.username(),
                    result.loginId(),
                    result.email(),
                    result.birthDate(),
                    result.gender()
            );
        }
    }

}
