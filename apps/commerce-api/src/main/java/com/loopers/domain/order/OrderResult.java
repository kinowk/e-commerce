package com.loopers.domain.order;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResult {
    public record GetOrderDetail(Long orderId, Long userId, Long totalPrice, OrderStatus status, List<Product> products) {
        public static GetOrderDetail from(Order order) {
            return new GetOrderDetail(
                    order.getId(),
                    order.getUserId(),
                    order.getTotalPrice(),
                    order.getStatus(),
                    order.getProducts()
                            .stream()
                            .map(product -> new Product(
                                    product.getId(),
                                    product.getOrderId(),
                                    product.getProductOptionId(),
                                    product.getPrice(),
                                    product.getQuantity()
                            ))
                            .toList()
            );
        }

    }

    public record Product(Long orderProductId, Long orderId, Long productOptionId, Integer price, Integer quantity) {

    }

    public record Create(Long orderId, Long userId, Long totalPrice, OrderStatus status, List<Product> products) {
        public record Product(Long orderProductId, Long orderId, Long productOptionId, Integer price, Integer quantity) {

        }

        public static Create from(Order order) {
            return new Create(
                    order.getId(),
                    order.getUserId(),
                    order.getTotalPrice(),
                    order.getStatus(),
                    order.getProducts().stream()
                            .map(product -> new Product(
                                    product.getId(),
                                    product.getOrderId(),
                                    product.getProductOptionId(),
                                    product.getPrice(),
                                    product.getQuantity()
                            ))
                            .toList()
            );
        }

    }

    public record GetOrderSummary(Long orderId, Long userId, Long totalPrice, OrderStatus status) {
        public static GetOrderSummary from(Order order) {
            return new GetOrderSummary(
                    order.getId(),
                    order.getUserId(),
                    order.getTotalPrice(),
                    order.getStatus()
            );
        }
    }
}
