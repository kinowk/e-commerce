package com.loopers.domain.coupon;

import com.loopers.domain.coupon.attribute.CouponType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResult {

    public record Use(Long couponId, CouponType type, Long discountAmount) {
        public static Use of(Coupon coupon, Long discountAmount) {
            return new Use(coupon.getId(), coupon.getType(), discountAmount);
        }
    }
}
