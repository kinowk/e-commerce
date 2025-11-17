package com.loopers.application.like;

import com.loopers.domain.like.LikeCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeInput {

    public record Like(Long userId, Long productId) {
        public LikeCommand.Like toCommand() {
            return new LikeCommand.Like(
                    userId,
                    productId
            );
        }
    }

    public record dislike(Long userId, Long productId) {
        public LikeCommand.Dislike toCommand() {
            return new LikeCommand.Dislike(
                    userId,
                    productId
            );
        }
    }

}
