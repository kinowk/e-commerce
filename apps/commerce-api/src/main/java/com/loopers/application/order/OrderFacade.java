package com.loopers.application.order;

import com.loopers.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;

    public OrderOutput.Create createOrder(OrderInput.Create input) {
        return OrderOutput.Create.from(orderService.createOrder(input.toCommand()));
    }

    public List<OrderOutput.Summary> getOrders(String userLoginId) {
        return orderService.getOrders(userLoginId)
                .stream()
                .map(OrderOutput.Summary::from)
                .toList();
    }

    public OrderOutput.Detail getOrder(String userLoginId, Long orderId) {
        return OrderOutput.Detail.from(orderService.getOrder(userLoginId, orderId));
    }
}
