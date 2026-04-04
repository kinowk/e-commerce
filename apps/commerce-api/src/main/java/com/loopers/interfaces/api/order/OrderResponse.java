package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderOutput;
import com.loopers.domain.order.attribute.OrderStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponse {

    public record Create(Long orderId, String userLoginId, Long totalAmount,
                         Long discountAmount, Long finalAmount,
                         OrderStatus status, List<Item> items) {
        public record Item(Long productId, Long quantity, Long unitPrice, Long totalPrice) {
            public static Item from(OrderOutput.Create.Item output) {
                return new Item(output.productId(), output.quantity(), output.unitPrice(), output.totalPrice());
            }
        }

        public static Create from(OrderOutput.Create output) {
            return new Create(
                    output.orderId(),
                    output.userLoginId(),
                    output.totalAmount(),
                    output.discountAmount(),
                    output.finalAmount(),
                    output.status(),
                    output.items().stream().map(Item::from).toList()
            );
        }
    }

    public record Summary(Long orderId, Long totalAmount, OrderStatus status) {
        public static Summary from(OrderOutput.Summary output) {
            return new Summary(output.orderId(), output.totalAmount(), output.status());
        }
    }

    public record Detail(Long orderId, String userLoginId, Long totalAmount,
                         OrderStatus status, List<Create.Item> items) {
        public static Detail from(OrderOutput.Detail output) {
            return new Detail(
                    output.orderId(),
                    output.userLoginId(),
                    output.totalAmount(),
                    output.status(),
                    output.items().stream().map(Create.Item::from).toList()
            );
        }
    }
}
