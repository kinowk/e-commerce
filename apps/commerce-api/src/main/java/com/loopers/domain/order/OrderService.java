package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public OrderResult.GetOrderSummary getOrderSummary(Long userId) {
        return orderRepository.findByUserId(userId)
                .map(OrderResult.GetOrderSummary::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public OrderResult.GetOrderDetail getOrderDetail(OrderCommand.GetOrderDetail command) {
        return orderRepository.findOrderDetailById(command.orderId())
                .filter(order -> Objects.equals(order.getUserId(), command.userId()))
                .map(OrderResult.GetOrderDetail::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문 상세 정보가 존재하지 않습니다."));
    }

    @Transactional
    public OrderResult.Create create(OrderCommand.Create command) {
        List<OrderCommand.Create.Product> products = command.products();
        if (products.isEmpty())
            throw new CoreException(ErrorType.BAD_REQUEST);

        Order order = Order.builder()
                .userId(command.userId())
                .totalPrice(command.totalPrice())
                .build();

        orderRepository.save(order);

        List<OrderProduct> orderProducts = command.products().stream()
                .map(product -> OrderProduct.builder()
                        .orderId(order.getId())
                        .productOptionId(product.productOptionId())
                        .quantity(product.quantity())
                        .price(product.price())
                        .build()
                )
                .toList();

        order.addProduct(orderProducts);

        return OrderResult.Create.from(order);
    }

    @Transactional
    public void complete(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문이 존재하지 않습니다."));

        order.complete();

        orderRepository.save(order);
    }

    @Transactional
    public void cancel(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문이 존재하지 않습니다."));

        order.cancel();

        orderRepository.save(order);
    }
}
