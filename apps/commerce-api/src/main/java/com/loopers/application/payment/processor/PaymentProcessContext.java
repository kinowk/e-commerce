package com.loopers.application.payment.processor;

import com.loopers.domain.order.OrderResult;
import com.loopers.domain.payment.PaymentMethod;

import java.util.List;

public record PaymentProcessContext(
        Long userId,
        Long orderId,
        List<Product> products,
        Long totalPrice
) {

    public static PaymentProcessContext of(Long userId, OrderResult.GetOrderDetail orderDetail) {
        List<Product> products = orderDetail.products()
                .stream()
                .map(product -> new Product(product.productOptionId(), product.quantity()))
                .toList();

        return new PaymentProcessContext(
                userId,
                orderDetail.orderId(),
                products,
                orderDetail.totalPrice()
        );
    }

    public record Product(Long productOptionId, Integer quantity) {

    }
}
