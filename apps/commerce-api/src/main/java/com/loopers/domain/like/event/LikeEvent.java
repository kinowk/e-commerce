package com.loopers.domain.like.event;

import com.loopers.domain.saga.event.SagaEvent;

import java.util.UUID;

public record LikeEvent() {

    public record Like(String eventId, String eventName, Long userId, Long productId) implements SagaEvent {
        public static Like from(Long userId, Long productId) {
            return new Like(
                    UUID.randomUUID().toString(),
                    "Like.Product.Like",
                    userId,
                    productId
            );
        }
    }

    public record Dislike(String eventId, String eventName, Long userId, Long productId) implements SagaEvent {
        public static Dislike from(Long userId, Long productId) {
            return new Dislike(
                    UUID.randomUUID().toString(),
                    "Like.Product.Dislike",
                    userId,
                    productId
            );
        }

    }
}
