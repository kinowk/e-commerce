package com.loopers.application.like;

import com.loopers.domain.like.LikeCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeInput {

    public record Toggle(String userLoginId, Long productId) {
        public LikeCommand.Toggle toCommand() {
            return new LikeCommand.Toggle(userLoginId, productId);
        }
    }
}
