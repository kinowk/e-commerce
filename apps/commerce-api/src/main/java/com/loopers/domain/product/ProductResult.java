package com.loopers.domain.product;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResult {

    public record GetProductDetail(Long productId, String productName, Integer basePrice, Long brandId, Long likeCount) {
        public static GetProductDetail from(Product product) {
            return new GetProductDetail(product.getId(), product.getName(), product.getBasePrice(), product.getBrandId(), product.getLikeCount());
        }
    }

    public record GetProductSummary(Integer totalPages, Long totalItems, Integer page, Integer size, List<Item> items) {
        public static GetProductSummary from(Page<ProductQueryResult.Products> page) {
            List<Item> items = page.map(content ->
                    new Item(
                            content.productId(),
                            content.productName(),
                            content.basePrice(),
                            content.likeCount(),
                            content.brandId(),
                            content.brandName()
                    )
            ).toList();

            return new GetProductSummary(
                    page.getTotalPages(),
                    page.getTotalElements(),
                    page.getPageable().getPageNumber(),
                    page.getPageable().getPageSize(),
                    items
            );
        }
    }

    public record Item(Long productId, String productName, Integer basePrice, Long likeCount, Long brandId, String brandName) {

    }

    public record GetProductOptions(List<Item> items) {
        public static GetProductOptions from(ProductQueryResult.ProductOptions queryResult) {
            return new GetProductOptions(
                    queryResult.items().stream()
                            .map(Item::from)
                            .toList()
            );
        }

        public record Item(Long productId, Long productOptionId, Integer sellingPrice, Integer stockQuantity) {
            public static Item from(ProductQueryResult.ProductOptions.Item item) {
                return new Item(
                        item.productId(),
                        item.productOptionId(),
                        item.sellingPrice(),
                        item.stockQuantity()
                );
            }
        }
    }
}
