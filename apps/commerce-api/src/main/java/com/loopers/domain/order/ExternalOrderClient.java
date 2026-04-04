package com.loopers.domain.order;

public interface ExternalOrderClient {
    void send(Long orderId);
}
