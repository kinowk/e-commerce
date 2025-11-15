package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderOutput;
import com.loopers.domain.order.OrderStatus;

import java.util.List;

public class OrderResponse {
    public record Create(Long orderId, Long totalPrice, OrderStatus status) {
        public static Create from(OrderOutput.Create output) {
            return new Create(
                    output.orderId(),
                    output.totalPrice(),
                    output.status()
            );
        }
    }

    public record GetOrderSummary(Long orderId, Long userId, Long totalPrice, OrderStatus status) {
        public static GetOrderSummary from(OrderOutput.GetOrderSummary output) {
            return new GetOrderSummary(
                    output.orderId(),
                    output.userId(),
                    output.totalPrice(),
                    output.status()
            );
        }
    }

    public record GetOrderDetail(Long orderId, Long userId, Long totalPrice, OrderStatus status, List<Product> products) {
        public static GetOrderDetail from(OrderOutput.GetOrderDetail output) {
            return new GetOrderDetail(
                    output.orderId(),
                    output.userId(),
                    output.totalPrice(),
                    output.status(),
                    output.products().stream()
                            .map(product -> new Product(
                                    product.orderProductId(),
                                    product.price(),
                                    product.quantity(),
                                    product.orderId(),
                                    product.productOptionId()
                            ))
                            .toList()
            );
        }

        public record Product(Long orderProductId, Integer price, Integer quantity, Long orderId, Long productOptionId) {

        }
    }
}
