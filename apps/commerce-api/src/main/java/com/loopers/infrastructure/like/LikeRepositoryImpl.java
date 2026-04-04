package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;

    @Override
    public boolean existsByUserLoginIdAndProductId(String userLoginId, Long productId) {
        return likeJpaRepository.existsByUserLoginIdAndProductId(userLoginId, productId);
    }

    @Override
    public Like save(Like like) {
        return likeJpaRepository.save(like);
    }

    @Override
    public void deleteByUserLoginIdAndProductId(String userLoginId, Long productId) {
        likeJpaRepository.deleteByUserLoginIdAndProductId(userLoginId, productId);
    }

    @Override
    public List<Long> findProductIdsByUserLoginId(String userLoginId) {
        return likeJpaRepository.findProductIdsByUserLoginId(userLoginId);
    }
}
