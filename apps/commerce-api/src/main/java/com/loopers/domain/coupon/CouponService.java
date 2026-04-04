package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Transactional
    public CouponResult.Use useForOrder(CouponCommand.Use command, long totalAmount) {
        Coupon coupon = couponRepository.findByIdForUpdate(command.couponId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

        if (!coupon.isOwnedBy(command.userId())) {
            throw new CoreException(ErrorType.BAD_REQUEST, "해당 쿠폰을 사용할 수 없습니다.");
        }
        if (coupon.isUsed()) {
            throw new CoreException(ErrorType.CONFLICT, "이미 사용된 쿠폰입니다.");
        }

        long discountAmount = coupon.calculateDiscount(totalAmount);
        coupon.use();
        couponRepository.save(coupon);

        return CouponResult.Use.of(coupon, discountAmount);
    }
}
