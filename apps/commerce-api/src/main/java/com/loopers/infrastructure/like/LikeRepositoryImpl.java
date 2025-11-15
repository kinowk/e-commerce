package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeQueryResult;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.like.QLike;
import com.loopers.domain.product.QProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository jpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long countByProductId(Long productId) {
        return jpaRepository.countByProductId(productId);
    }

    @Override
    public Optional<Like> findByUserIdAndProductId(Long userId, Long productId) {
        return jpaRepository.findByUserIdAndProductId(userId, productId);
    }

    @Override
    public void delete(Like like) {
        jpaRepository.delete(like);
    }

    @Override
    public Like save(Like like) {
        return jpaRepository.save(like);
    }

    @Override
    public List<LikeQueryResult.GetLikeProducts> findByUserId(Long userId) {
        QLike l = QLike.like;
        QProduct p = QProduct.product;

        return jpaQueryFactory
                .select(
                        l.id,
                        l.userId,
                        l.productId,
                        p.name
                )
                .from(l)
                .join(p).on(p.id.eq(l.productId))
                .where(l.userId.eq(userId))
                .stream()
                .map(row -> new LikeQueryResult.GetLikeProducts(
                        row.get(l.id),
                        row.get(l.userId),
                        row.get(l.productId),
                        row.get(p.name)
                ))
                .toList();
    }
}
