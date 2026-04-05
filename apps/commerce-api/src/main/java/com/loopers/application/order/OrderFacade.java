package com.loopers.application.order;

import com.loopers.domain.order.ExternalOrderClient;
import com.loopers.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final ExternalOrderClient externalOrderClient;

    public OrderOutput.Create createOrder(OrderInput.Create input) {
        OrderOutput.Create result = OrderOutput.Create.from(orderService.createOrder(input.toCommand()));
        externalOrderClient.send(result.orderId());
        return result;
    }

    public List<OrderOutput.Summary> getOrders(Long userId) {
        return orderService.getOrders(userId)
                .stream()
                .map(OrderOutput.Summary::from)
                .toList();
    }

    public OrderOutput.Detail getOrder(Long userId, Long orderId) {
        return OrderOutput.Detail.from(orderService.getOrder(userId, orderId));
    }
}
