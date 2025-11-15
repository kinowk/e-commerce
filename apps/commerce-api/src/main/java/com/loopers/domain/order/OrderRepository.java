package com.loopers.domain.order;

import java.util.Optional;

public interface OrderRepository {

    Optional<Order> findById(Long id);

    Optional<Order> findByUserId(Long userId);

    Optional<Order> findOrderDetailById(Long orderId);

    Order save(Order order);
}
