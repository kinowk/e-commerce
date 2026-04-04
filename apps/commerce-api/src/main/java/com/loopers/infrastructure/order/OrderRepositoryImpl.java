package com.loopers.infrastructure.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public OrderItem saveItem(OrderItem item) {
        return orderItemJpaRepository.save(item);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id);
    }

    @Override
    public List<Order> findByUserLoginId(String userLoginId) {
        return orderJpaRepository.findAllByUserLoginIdOrderByCreatedAtDesc(userLoginId);
    }

    @Override
    public List<OrderItem> findItemsByOrderId(Long orderId) {
        return orderItemJpaRepository.findAllByOrderId(orderId);
    }
}
