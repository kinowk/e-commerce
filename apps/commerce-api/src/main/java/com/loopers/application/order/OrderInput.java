package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInput {

    public record GetOrderDetail(Long userId, Long orderId) {
        public OrderCommand.GetOrderDetail toCommand() {
            return new OrderCommand.GetOrderDetail(
                    userId,
                    orderId
            );
        }
    }

    public record Create(Long userId, List<Product> products) {

        public record Product(Long productOptionId, Integer quantity) {

        }
    }
}
