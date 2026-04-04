package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductOutput;
import com.loopers.domain.product.attribute.ProductStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponse {

    public record Detail(Long id, Long brandId, String brandName, String name,
                         String description, Long price, Long stock,
                         Long likeCount, ProductStatus status) {
        public static Detail from(ProductOutput.Detail output) {
            return new Detail(
                    output.id(), output.brandId(), output.brandName(),
                    output.name(), output.description(), output.price(),
                    output.stock(), output.likeCount(), output.status()
            );
        }
    }

    public record Summary(Long id, String brandName, String name,
                          Long price, Long likeCount, ProductStatus status) {
        public static Summary from(ProductOutput.Summary output) {
            return new Summary(
                    output.id(), output.brandName(), output.name(),
                    output.price(), output.likeCount(), output.status()
            );
        }
    }

    public record List(java.util.List<Summary> products) {
        public static List from(ProductOutput.List output) {
            return new List(output.products().stream().map(Summary::from).toList());
        }
    }
}
