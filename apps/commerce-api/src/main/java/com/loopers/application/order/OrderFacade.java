package com.loopers.application.order;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.attribute.OrderStatus;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderOutput.Create createOrder(OrderInput.Create input) {
        OrderOutput.Create result = OrderOutput.Create.from(orderService.createOrder(input.toCommand()));

        if (result.finalAmount() > 0) {
            paymentService.requestPayment(new PaymentCommand.Request(
                    result.orderId(), input.cardType(), input.cardNo(), result.finalAmount()
            ));
        } else {
            orderService.updateOrderStatus(result.orderId(), OrderStatus.PAID);
        }

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
