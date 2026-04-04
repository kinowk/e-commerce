package com.loopers.domain.coupon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponCommand {

    public record Use(Long couponId, Long userId) {
    }
}
