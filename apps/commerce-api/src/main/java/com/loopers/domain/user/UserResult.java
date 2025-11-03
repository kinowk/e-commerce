package com.loopers.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResult {

    @Getter
    @Builder
    public static class Join {
        private final Long id;
        private final String loginId;
        private final Gender gender;
        private final String birthDate;
        private final String email;

        public static Join from(User user) {
            return Join.builder()
                    .id(user.getId())
                    .loginId(user.getLoginId())
                    .gender(user.getGender())
                    .birthDate(user.getBirthDate())
                    .email(user.getEmail())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetUser {
        private final Long id;
        private final String loginId;
        private final Gender gender;
        private final String birthDate;
        private final String email;

        public static GetUser from(User user) {
            return GetUser.builder()
                    .id(user.getId())
                    .loginId(user.getLoginId())
                    .gender(user.getGender())
                    .birthDate(user.getBirthDate())
                    .email(user.getEmail())
                    .build();
        }
    }
}
