package com.loopers.domain.product;

import com.loopers.domain.product.attribute.ProductSortType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCommand {

    public record Query(Long brandId, ProductSortType sortType, int page, int size) {
    }
}
