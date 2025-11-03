package com.loopers.application.user;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserOutput {

    public record Join(Long id, String loginId, Gender gender, String birthDate, String email) {
        public static Join from(UserResult.Join result) {
            return new Join(
                    result.getId(),
                    result.getLoginId(),
                    result.getGender(),
                    result.getBirthDate(),
                    result.getEmail()
            );
        }
    }

    public record GetUser(Long id, String loginId, Gender gender, String birthDate, String email) {
        public static GetUser from(UserResult.GetUser result) {
            return new GetUser(
                    result.getId(),
                    result.getLoginId(),
                    result.getGender(),
                    result.getBirthDate(),
                    result.getEmail()
            );
        }

    }

}
