package com.loopers.domain.coupon;

import java.util.Optional;

public interface CouponRepository {

    Optional<Coupon> findById(Long id);

    Optional<Coupon> findByUserId(Long userId);

    Coupon save(Coupon coupon);
}
