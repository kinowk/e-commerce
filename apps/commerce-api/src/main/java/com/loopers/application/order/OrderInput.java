package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInput {

    public record Create(String userLoginId, List<Item> items, Long couponId) {
        public record Item(Long productId, Long quantity) {
            public OrderCommand.Create.Item toCommand() {
                return new OrderCommand.Create.Item(productId, quantity);
            }
        }

        public OrderCommand.Create toCommand() {
            return new OrderCommand.Create(
                    userLoginId,
                    items.stream().map(Item::toCommand).toList(),
                    couponId
            );
        }
    }
}
