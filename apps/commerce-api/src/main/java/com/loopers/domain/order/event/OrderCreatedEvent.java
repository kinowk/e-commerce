package com.loopers.domain.order.event;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        Long couponId,
        String cardType,
        String cardNo,
        Long finalAmount
) {
}
