package com.loopers.domain.order;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCommand {
    public record GetOrderDetail(Long userId, Long orderId) {
    }

    public record Create(Long userId, Long totalPrice, List<Product> products) {

        public record Product(Long productOptionId, Integer price, Integer quantity) {

        }
    }
}
