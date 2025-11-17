package com.loopers.application.like;

import com.loopers.domain.like.LikeResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeOutput {

    public record LikeProducts(List<Product> products) {
        public static LikeOutput.LikeProducts from(LikeResult.GetLikeProducts result) {
            List<Product> products = result.products()
                    .stream()
                    .map(product -> new Product(
                            product.productId(),
                            product.productName()
                    ))
                    .toList();

            return new LikeOutput.LikeProducts(products);
        }
    }

    public record Product(Long productId, String productName) {

    }
}
