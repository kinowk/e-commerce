package com.loopers.domain.like;

import java.util.List;

public interface LikeRepository {

    Long countByProductId(Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void delete(Like like);

    Like save(Like like);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    List<LikeQueryResult.GetLikeProducts> findByUserId(Long userId);
}
