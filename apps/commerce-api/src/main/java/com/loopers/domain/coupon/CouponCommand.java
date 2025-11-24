package com.loopers.domain.coupon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponCommand {
    public record Create(Long userId, String couponName, DiscountPolicy discountPolicy, Long amount, ZonedDateTime expiresAt) {
    }

    public record Use(Long userId, List<Long> userCouponIds) {
    }
}
