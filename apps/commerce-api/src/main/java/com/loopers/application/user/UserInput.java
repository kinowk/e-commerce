package com.loopers.application.user;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInput {

    public record Join(String loginId, Gender gender, String birthDate, String email) {
        public UserCommand.Join toCommand() {
            return new UserCommand.Join(
                    loginId,
                    gender,
                    birthDate,
                    email
            );
        }

    }

}
