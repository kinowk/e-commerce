package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Optional<OrderResult.GetOrderDetail> getOrderDetail(OrderCommand.GetOrderDetail command) {
        Long orderId = command.orderId();
        return orderRepository.findOrderDetailById(orderId)
                .filter(order -> Objects.equals(order.getUserId(), command.userId()))
                .map(OrderResult.GetOrderDetail::from);
    }

    @Transactional
    public OrderResult.Create create(OrderCommand.Create command) {
        List<OrderCommand.Create.Product> products = command.products();
        if (products.isEmpty())
            throw new CoreException(ErrorType.BAD_REQUEST);

        Order order = new Order(command.userId(), command.totalPrice());

        List<OrderProduct> orderProducts = command.products().stream()
                .map(product -> new OrderProduct(
                        order.getId(),
                        product.productOptionId(),
                        product.quantity(),
                        product.price()
                ))
                .toList();

        order.addProduct(orderProducts);

        orderRepository.save(order);

        return OrderResult.Create.from(order);
    }
}
