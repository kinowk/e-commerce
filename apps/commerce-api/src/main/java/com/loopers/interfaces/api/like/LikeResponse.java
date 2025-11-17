package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeOutput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeResponse {

    public record GetLikeProducts(List<Product> products) {

        public static GetLikeProducts from(LikeOutput.LikeProducts output) {
            List<Product> products = output.products()
                    .stream()
                    .map(product -> new Product(
                            product.productId(),
                            product.productName()
                    ))
                    .toList();

            return new GetLikeProducts(products);
        }
    }

    public record Product(Long productId, String productName) {

    }
}
