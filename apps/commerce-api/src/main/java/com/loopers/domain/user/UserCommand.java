package com.loopers.domain.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCommand {

    public record Join(String loginId, Gender gender, String birthDate, String email) {

    }

}
