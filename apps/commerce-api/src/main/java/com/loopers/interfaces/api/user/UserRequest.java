package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserInput;
import com.loopers.domain.user.attribute.Gender;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserRequest {

    public record Join(String username, String loginId, String password, String email, String birthDate, Gender gender) {
        public UserInput.Join toInput() {
            return new UserInput.Join(
                    username,
                    loginId,
                    password,
                    email,
                    birthDate,
                    gender
            );
        }
    }
}
