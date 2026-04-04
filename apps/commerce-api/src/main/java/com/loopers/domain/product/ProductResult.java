package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.attribute.ProductStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResult {

    public record Detail(Long id, Long brandId, String brandName, String name,
                         String description, Long price, Long stock,
                         Long likeCount, ProductStatus status) {
        public static Detail of(Product product, Brand brand) {
            return new Detail(
                    product.getId(),
                    product.getBrandId(),
                    brand.getName(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getStock(),
                    product.getLikeCount(),
                    product.getStatus()
            );
        }
    }

    public record Summary(Long id, Long brandId, String brandName, String name,
                          Long price, Long likeCount, ProductStatus status) {
        public static Summary of(Product product, Brand brand) {
            return new Summary(
                    product.getId(),
                    product.getBrandId(),
                    brand.getName(),
                    product.getName(),
                    product.getPrice(),
                    product.getLikeCount(),
                    product.getStatus()
            );
        }
    }

    public record List(java.util.List<Summary> products) {
        public static List of(java.util.List<Product> products, Map<Long, Brand> brandMap) {
            return new List(
                    products.stream()
                            .map(p -> Summary.of(p, brandMap.get(p.getBrandId())))
                            .toList()
            );
        }
    }
}
