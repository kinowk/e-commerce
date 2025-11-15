package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductInput;
import com.loopers.domain.product.ProductSortType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRequest {
    public record GetProductList(String keyword, Long brandId, ProductSortType sort, Integer page, Integer size) {
        public ProductInput.GetProductList toInput() {
            return new ProductInput.GetProductList(keyword, brandId, sort, page, size);
        }
    }

}
