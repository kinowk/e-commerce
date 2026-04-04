package com.loopers.application.like;

import com.loopers.domain.like.LikeCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeInput {

    public record Toggle(Long userId, Long productId) {
        public LikeCommand.Toggle toCommand() {
            return new LikeCommand.Toggle(userId, productId);
        }
    }
}
