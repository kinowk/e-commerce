package com.loopers.domain.order;

import com.loopers.domain.order.attribute.OrderStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResult {

    public record Create(Long orderId, Long userId, Long totalAmount,
                         Long discountAmount, Long finalAmount,
                         OrderStatus status, List<Item> items) {
        public record Item(Long productId, Long quantity, Long unitPrice, Long totalPrice) {
            public static Item from(OrderItem orderItem) {
                return new Item(
                        orderItem.getProductId(),
                        orderItem.getQuantity(),
                        orderItem.getUnitPrice(),
                        orderItem.getTotalPrice()
                );
            }
        }

        public static Create of(Order order, List<OrderItem> items) {
            return new Create(
                    order.getId(),
                    order.getUserId(),
                    order.getTotalAmount(),
                    order.getDiscountAmount(),
                    order.getFinalAmount(),
                    order.getStatus(),
                    items.stream().map(Item::from).toList()
            );
        }
    }

    public record Summary(Long orderId, Long totalAmount, OrderStatus status) {
        public static Summary from(Order order) {
            return new Summary(order.getId(), order.getTotalAmount(), order.getStatus());
        }
    }

    public record Detail(Long orderId, Long userId, Long totalAmount,
                         OrderStatus status, List<Create.Item> items) {
        public static Detail of(Order order, List<OrderItem> items) {
            return new Detail(
                    order.getId(),
                    order.getUserId(),
                    order.getTotalAmount(),
                    order.getStatus(),
                    items.stream().map(Create.Item::from).toList()
            );
        }
    }
}
