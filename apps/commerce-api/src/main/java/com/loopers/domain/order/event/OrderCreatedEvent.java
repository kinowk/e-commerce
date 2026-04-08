package com.loopers.domain.order.event;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        Long couponId,
        String cardType,
        String cardNo,
        Long finalAmount,
        List<Item> items
) {
    public record Item(Long productId, Long quantity, Long unitPrice) {
    }
}
