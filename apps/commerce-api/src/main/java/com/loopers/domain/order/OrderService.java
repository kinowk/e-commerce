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

        Order order = new Order(command.userId(), command.totalPrice());

        orderRepository.save(order);

        List<OrderProduct> orderProducts = command.products().stream()
                .map(product -> new OrderProduct(
                        order.getId(),
                        product.productOptionId(),
                        product.quantity(),
                        product.price()
                ))
                .toList();

        order.addProduct(orderProducts);

        return OrderResult.Create.from(order);
    }

    @Transactional(readOnly = true)
    public OrderResult.GetOrderSummary getOrderSummary(Long userId) {
        return orderRepository.findByUserId(userId)
                .map(OrderResult.GetOrderSummary::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }
}
