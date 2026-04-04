package com.loopers.application.order;

import com.loopers.domain.order.OrderResult;
import com.loopers.domain.order.attribute.OrderStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderOutput {

    public record Create(Long orderId, Long userId, Long totalAmount,
                         Long discountAmount, Long finalAmount,
                         OrderStatus status, List<Item> items) {
        public record Item(Long productId, Long quantity, Long unitPrice, Long totalPrice) {
            public static Item from(OrderResult.Create.Item result) {
                return new Item(result.productId(), result.quantity(), result.unitPrice(), result.totalPrice());
            }
        }

        public static Create from(OrderResult.Create result) {
            return new Create(
                    result.orderId(),
                    result.userId(),
                    result.totalAmount(),
                    result.discountAmount(),
                    result.finalAmount(),
                    result.status(),
                    result.items().stream().map(Item::from).toList()
            );
        }
    }

    public record Summary(Long orderId, Long totalAmount, OrderStatus status) {
        public static Summary from(OrderResult.Summary result) {
            return new Summary(result.orderId(), result.totalAmount(), result.status());
        }
    }

    public record Detail(Long orderId, Long userId, Long totalAmount,
                         OrderStatus status, List<Create.Item> items) {
        public static Detail from(OrderResult.Detail result) {
            return new Detail(
                    result.orderId(),
                    result.userId(),
                    result.totalAmount(),
                    result.status(),
                    result.items().stream().map(Create.Item::from).toList()
            );
        }
    }
}
