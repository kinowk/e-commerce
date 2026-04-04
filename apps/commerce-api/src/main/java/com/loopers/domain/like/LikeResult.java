package com.loopers.domain.like;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeResult {

    public record Toggle(Long productId, Long likeCount) {
    }

    public record LikedProductIds(List<Long> productIds) {
    }
}
