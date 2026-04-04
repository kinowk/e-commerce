package com.loopers.domain.like;

import java.util.List;

public interface LikeRepository {
    boolean existsByUserLoginIdAndProductId(String userLoginId, Long productId);
    Like save(Like like);
    void deleteByUserLoginIdAndProductId(String userLoginId, Long productId);
    List<Long> findProductIdsByUserLoginId(String userLoginId);
}
