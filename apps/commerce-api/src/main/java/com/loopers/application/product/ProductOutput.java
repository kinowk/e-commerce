package com.loopers.application.product;

import com.loopers.domain.brand.BrandResult;
import com.loopers.domain.product.ProductResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductOutput {

    public record GetProductSummary(
            Integer totalPages,
            Long totalItems,
            Integer page,
            Integer size,
            List<ProductResult.Item> items
    ) {
        public static GetProductSummary from(ProductResult.GetProductSummary result) {
            List<ProductResult.Item> items = result.items().stream()
                    .map(item -> new ProductResult.Item(
                            item.productId(),
                            item.productName(),
                            item.basePrice(),
                            item.likeCount(),
                            item.brandId(),
                            item.brandName()
                    ))
                    .toList();
            return new GetProductSummary(
                    result.totalPages(),
                    result.totalItems(),
                    result.page(),
                    result.size(),
                    items
            );
        }
    }

    public record GetProductDetail(Long productId, String productName, Integer basePrice, Long likeCount, Long brandId, String brandName, String brandDescription) {
        public static GetProductDetail from(ProductResult.GetProductDetail productResult, Long likeCount, BrandResult.GetBrand brandResult) {
            return new GetProductDetail(
                    productResult.productId(),
                    productResult.productName(),
                    productResult.basePrice(),
                    likeCount,
                    brandResult.getId(),
                    brandResult.getName(),
                    brandResult.getDescription()
            );
        }

    }
}
