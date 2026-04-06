package com.loopers.domain.like.event;

public record LikeToggledEvent(
        Long userId,
        Long productId,
        boolean added  // true=좋아요, false=좋아요 취소
) {
}
