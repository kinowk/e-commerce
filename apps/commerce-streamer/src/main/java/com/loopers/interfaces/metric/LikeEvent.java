package com.loopers.interfaces.metric;

public record LikeEvent() {

    public record Like(String eventId, String eventName, Long userId, Long productId) {

    }

    public record Dislike(String eventId, String eventName, Long userId, Long productId) {

    }

}
