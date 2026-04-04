package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {
    boolean existsByUserLoginIdAndProductId(String userLoginId, Long productId);
    void deleteByUserLoginIdAndProductId(String userLoginId, Long productId);

    @Query("SELECT l.productId FROM Like l WHERE l.userLoginId = :userLoginId")
    List<Long> findProductIdsByUserLoginId(@Param("userLoginId") String userLoginId);
}
