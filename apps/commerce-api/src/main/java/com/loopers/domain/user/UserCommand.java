package com.loopers.domain.user;

import com.loopers.domain.user.attribute.Gender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCommand {
    public record Join(String username, String loginId, String password, String email, String birthDate, Gender gender) {
    }
}
