package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInput {

    public record Create(Long userId, List<Item> items, Long couponId, String cardType, String cardNo) {
        public record Item(Long productId, Long quantity) {
            public OrderCommand.Create.Item toCommand() {
                return new OrderCommand.Create.Item(productId, quantity);
            }
        }

        public OrderCommand.Create toCommand() {
            return new OrderCommand.Create(
                    userId,
                    items.stream().map(Item::toCommand).toList(),
                    couponId
            );
        }
    }
}
