package com.loopers.domain.like;

import java.util.List;

public interface LikeRepository {
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    Like save(Like like);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    List<Long> findProductIdsByUserId(Long userId);
}
