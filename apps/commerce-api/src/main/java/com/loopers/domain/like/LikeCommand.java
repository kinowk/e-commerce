package com.loopers.domain.like;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeCommand {

    public record Like(Long userId, Long productId) {
    }

    public record Dislike(Long userId, Long productId) {
    }
}
