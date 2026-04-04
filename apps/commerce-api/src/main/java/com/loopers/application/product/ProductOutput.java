package com.loopers.application.product;

import com.loopers.domain.product.ProductResult;
import com.loopers.domain.product.attribute.ProductStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductOutput {

    public record Detail(Long id, Long brandId, String brandName, String name,
                         String description, Long price, Long stock,
                         Long likeCount, ProductStatus status) {
        public static Detail from(ProductResult.Detail result) {
            return new Detail(result.id(), result.brandId(), result.brandName(),
                    result.name(), result.description(), result.price(),
                    result.stock(), result.likeCount(), result.status());
        }
    }

    public record Summary(Long id, String brandName, String name,
                          Long price, Long likeCount, ProductStatus status) {
        public static Summary from(ProductResult.Summary result) {
            return new Summary(result.id(), result.brandName(), result.name(),
                    result.price(), result.likeCount(), result.status());
        }
    }

    public record List(java.util.List<Summary> products) {
        public static List from(ProductResult.List result) {
            return new List(result.products().stream().map(Summary::from).toList());
        }
    }
}
