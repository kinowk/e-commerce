package com.loopers.domain.product.event;

public interface ProductEventPublisher {

    void publishEvent(ProductEvent.Like event);

    void publishEvent(ProductEvent.Dislike event);
}
