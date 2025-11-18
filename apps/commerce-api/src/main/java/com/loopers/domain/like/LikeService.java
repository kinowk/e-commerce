package com.loopers.domain.like;

import com.loopers.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public LikeResult.GetLikeProducts getLikeProducts(Long userId) {
        List<LikeQueryResult.GetLikeProducts> result = likeRepository.findByUserId(userId);
        return LikeResult.GetLikeProducts.from(result);

    }

    @Transactional(readOnly = true)
    public Long getLikeCount(Long productId) {
        return likeRepository.countByProductId(productId);
    }

    @Transactional
    public void like(LikeCommand.Like command) {
        Long userId = command.userId();
        Long productId = command.productId();

        if (likeRepository.existsByUserIdAndProductId(userId, productId))
            return;

        Like like = Like.builder()
                .userId(userId)
                .productId(productId)
                .build();

        likeRepository.save(like);

        productRepository.increaseLikeCount(productId);
    }

    @Transactional
    public void dislike(LikeCommand.Dislike command) {
        Long userId = command.userId();
        Long productId = command.productId();

        if (!likeRepository.existsByUserIdAndProductId(userId, productId))
            return;


        likeRepository.deleteByUserIdAndProductId(userId, productId);

        productRepository.decreaseLikeCount(productId);
    }
}
