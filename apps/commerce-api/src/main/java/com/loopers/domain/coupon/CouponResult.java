package com.loopers.domain.coupon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResult {

    public record GetCoupon(Long id, Long userId, String name, DiscountPolicy discountPolicy, Long amount, ZonedDateTime createdAt, ZonedDateTime usedAt, ZonedDateTime expiresAt) {
        public static GetCoupon from(Coupon coupon) {
            return new GetCoupon(
                    coupon.getId(),
                    coupon.getUserId(),
                    coupon.getName(),
                    coupon.getDiscountPolicy(),
                    coupon.getAmount(),
                    coupon.getCreatedAt(),
                    coupon.getUsedAt(),
                    coupon.getExpiresAt()
            );
        }
    }

    public record Create(Long id, Long userId, String name, DiscountPolicy discountPolicy, Long amount, ZonedDateTime createdAt, ZonedDateTime expiresAt) {
        public static Create from(Coupon coupon) {
            return new Create(
                    coupon.getId(),
                    coupon.getUserId(),
                    coupon.getName(),
                    coupon.getDiscountPolicy(),
                    coupon.getAmount(),
                    coupon.getCreatedAt(),
                    coupon.getExpiresAt()
            );
        }
    }

}
