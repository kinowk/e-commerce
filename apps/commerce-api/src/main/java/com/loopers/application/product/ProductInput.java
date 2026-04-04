package com.loopers.application.product;

import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.attribute.ProductSortType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInput {

    public record Query(Long brandId, ProductSortType sortType, int page, int size) {
        public ProductCommand.Query toCommand() {
            return new ProductCommand.Query(brandId, sortType, page, size);
        }
    }
}
