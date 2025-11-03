package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserInput;
import com.loopers.domain.user.Gender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequest {

    public record Join(String loginId, Gender gender, String birthDate, String email) {
        public UserInput.Join toInput() {
            return new UserInput.Join(loginId, gender, birthDate, email);
        }
    }

}
