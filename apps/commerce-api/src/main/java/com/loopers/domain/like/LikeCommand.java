package com.loopers.domain.like;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeCommand {

    public record Toggle(String userLoginId, Long productId) {
    }
}
