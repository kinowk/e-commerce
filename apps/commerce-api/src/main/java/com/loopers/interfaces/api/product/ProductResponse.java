package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductOutput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponse {
    public record GetProductSummary() {
        public static GetProductSummary from(ProductOutput.GetProductSummary output) {
            return new GetProductSummary();
        }
    }

    public record GetProductDetail() {
        public static GetProductDetail from(ProductOutput.GetProductDetail output) {
            return new GetProductDetail();
        }

    }
}
