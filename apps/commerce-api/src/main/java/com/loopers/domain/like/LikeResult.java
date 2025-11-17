package com.loopers.domain.like;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeResult {

    public record GetLikeProducts(List<Product> products) {
        public static GetLikeProducts from(List<LikeQueryResult.GetLikeProducts> queryResults) {
            List<Product> products = queryResults.stream()
                    .map(queryResult -> new Product(
                            queryResult.likeProductId(),
                            queryResult.userId(),
                            queryResult.productId(),
                            queryResult.productName()
                    ))
                    .toList();

            return new GetLikeProducts(products);
        }
    }

    public record Product(Long productLikeId, Long userId, Long productId, String productName) {

    }
}
