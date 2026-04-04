package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderInput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {

    public record Create(List<Item> items) {
        public record Item(Long productId, Long quantity) {
            public OrderInput.Create.Item toInput() {
                return new OrderInput.Create.Item(productId, quantity);
            }
        }

        public OrderInput.Create toInput(String userLoginId) {
            return new OrderInput.Create(
                    userLoginId,
                    items.stream().map(Item::toInput).toList()
            );
        }
    }
}
