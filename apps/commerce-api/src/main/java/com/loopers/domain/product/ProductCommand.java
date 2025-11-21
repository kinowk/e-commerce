package com.loopers.domain.product;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCommand {

    public record GetProductSummary(String keyword, Long brandId, ProductSortType sortType, Integer page, Integer size) {
    }

    public record DeductStocks(List<Item> items) {
        public record Item(Long productOptionId, Integer amount) {

        }
    }

    public record AddStocks(List<Item> items) {
        public record Item(Long productOptionId, Integer amount) {

        }
    }
}
