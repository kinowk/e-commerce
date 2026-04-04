package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductInput;
import com.loopers.domain.product.attribute.ProductSortType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRequest {

    public record Query(Long brandId, ProductSortType sortType, int page, int size) {
        public ProductInput.Query toInput() {
            return new ProductInput.Query(brandId, sortType, page, size);
        }
    }
}
