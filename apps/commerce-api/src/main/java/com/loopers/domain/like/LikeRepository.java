package com.loopers.domain.like;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {

    Long countByProductId(Long productId);

    Optional<Like> findByUserIdAndProductId(Long userId, Long productId);

    void delete(Like like);

    Like save(Like like);

    List<LikeQueryResult.GetLikeProducts> findByUserId(Long userId);
}
