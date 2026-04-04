package com.loopers.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    OrderItem saveItem(OrderItem item);
    Optional<Order> findById(Long id);
    List<Order> findByUserLoginId(String userLoginId);
    List<OrderItem> findItemsByOrderId(Long orderId);
}
