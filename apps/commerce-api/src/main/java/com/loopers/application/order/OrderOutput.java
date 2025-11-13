package com.loopers.application.order;

import com.loopers.domain.order.OrderResult;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.payment.PaymentResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderOutput {
    public record GetOrderDetail(Long orderId, Long userId, Long totalPrice, OrderStatus status, List<Product> products) {
        public static GetOrderDetail from(OrderResult.GetOrderDetail order, PaymentResult.GetPayment payment) {
            return new GetOrderDetail(
                    order.orderId(),
                    order.userId(),
                    order.totalPrice(),
                    order.status(),
                    order.products().stream()
                            .map(product -> new Product(
                                    product.orderProductId(),
                                    product.orderId(),
                                    product.productOptionId(),
                                    product.price(),
                                    product.quantity()
                            ))
                            .toList()
            );
        }

        public record Product(Long orderProductId, Long orderId, Long productOptionId, Integer price, Integer quantity) {

        }
    }

    public record Create(Long orderId, Long totalPrice, OrderStatus status) {
        public static Create from(OrderResult.Create result) {
            return new Create(
                    result.orderId(),
                    result.totalPrice(),
                    result.status()
            );
        }
    }
}
