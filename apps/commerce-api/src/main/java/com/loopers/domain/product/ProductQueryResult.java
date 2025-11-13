package com.loopers.domain.product;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductQueryResult {

    public record Products(Long productId, String productName, Integer basePrice, Long likeCount, Long brandId, String brandName) {
    }

    public record ProductOptions(List<Item> items) {
        public record Item(Long productId, Long productOptionId, Integer sellingPrice, Integer stockQuantity) {

        }
    }
}
