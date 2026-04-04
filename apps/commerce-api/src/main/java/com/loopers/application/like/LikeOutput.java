package com.loopers.application.like;

import com.loopers.domain.like.LikeResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeOutput {

    public record Toggle(Long productId, Long likeCount) {
        public static Toggle from(LikeResult.Toggle result) {
            return new Toggle(result.productId(), result.likeCount());
        }
    }

    public record LikedProductIds(List<Long> productIds) {
        public static LikedProductIds from(LikeResult.LikedProductIds result) {
            return new LikedProductIds(result.productIds());
        }
    }
}
