package com.loopers.application.user;

import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.attribute.Gender;

public class UserInput {

    public record Join(String username, String loginId, String password, String email, String birthDate, Gender gender) {
        public UserCommand.Join toCommand() {
            return new UserCommand.Join(
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
