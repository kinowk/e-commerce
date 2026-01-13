package com.loopers.domain.user;

import com.loopers.domain.user.attribute.Gender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResult {
    public record Join(Long id, String username, String loginId, String email, String birthDate, Gender gender) {
        public static Join from(User user) {
            return new Join(
                    user.getId(),
                    user.getUsername(),
                    user.getLoginId(),
                    user.getEmail(),
                    user.getBirthDate(),
                    user.getGender()
            );
        }
    }

    public record GetUser(Long id, String username, String loginId, String email, String birthDate, Gender gender) {
        public static GetUser from(User user) {
            return new GetUser(
                    user.getId(),
                    user.getUsername(),
                    user.getLoginId(),
                    user.getEmail(),
                    user.getBirthDate(),
                    user.getGender()
            );
        }
    }
}
