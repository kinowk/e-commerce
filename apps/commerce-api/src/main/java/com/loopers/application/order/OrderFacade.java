package com.loopers.application.order;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderOutput.Create createOrder(OrderInput.Create input) {
        OrderOutput.Create result = OrderOutput.Create.from(orderService.createOrder(input.toCommand()));
        eventPublisher.publishEvent(new OrderCreatedEvent(
                result.orderId(), result.userId(), input.couponId(),
                input.cardType(), input.cardNo(), result.finalAmount()
        ));
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
