package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeOutput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeResponse {

    public record Toggle(Long productId, Long likeCount) {
        public static Toggle from(LikeOutput.Toggle output) {
            return new Toggle(output.productId(), output.likeCount());
        }
    }

    public record LikedProductIds(List<Long> productIds) {
        public static LikedProductIds from(LikeOutput.LikedProductIds output) {
            return new LikedProductIds(output.productIds());
        }
    }
}
