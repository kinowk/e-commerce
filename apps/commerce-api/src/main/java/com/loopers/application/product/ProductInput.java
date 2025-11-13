package com.loopers.application.product;

import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductSortType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInput {
    public record GetProductList(String keyword, Long brandId, ProductSortType sortType, Integer page, Integer size) {
        public ProductCommand.GetProductSummary toCommand() {
            return new ProductCommand.GetProductSummary(keyword, brandId, sortType, page, size);
        }
    }

}
