package com.loopers.domain.order;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCommand {

    public record Create(String userLoginId, List<Item> items, Long couponId) {
        public record Item(Long productId, Long quantity) {
        }
    }
}
