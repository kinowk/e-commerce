package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public CouponResult.GetCoupon getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰이 존재하지 않습니다."));

        return CouponResult.GetCoupon.from(coupon);
    }

    @Transactional
    public CouponResult.Create createCoupon(CouponCommand.Create command) {
        Coupon coupon = Coupon.builder()
                .userId(command.userId())
                .name(command.couponName())
                .discountPolicy(command.discountPolicy())
                .amount(command.amount())
                .expiresAt(command.expiresAt())
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);
        return CouponResult.Create.from(savedCoupon);
    }

    @Transactional
    public void use(CouponCommand.Use command) {
        List<Long> couponIds = command.userCouponIds();
        if (couponIds.isEmpty())
            return;

        Long userId = command.userId();
    }

}
