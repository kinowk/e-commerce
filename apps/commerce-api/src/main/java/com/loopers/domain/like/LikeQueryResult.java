package com.loopers.domain.like;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeQueryResult {
    public record GetLikeProducts(Long likeProductId, Long userId, Long productId, String productName) {
    }
}
