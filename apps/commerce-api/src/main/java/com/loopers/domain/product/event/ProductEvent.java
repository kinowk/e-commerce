package com.loopers.domain.product.event;

import com.loopers.domain.product.Product;

public interface ProductEvent {

    record Like(Long productId, Long likeCount) {
        public static Like from(Product product) {
            return new Like(product.getId(), product.getLikeCount());
        }
    }

    record Dislike(Long productId, Long likeCount) {
        public static Dislike from(Product product) {
            return new Dislike(product.getId(), product.getLikeCount());
        }
    }

}
