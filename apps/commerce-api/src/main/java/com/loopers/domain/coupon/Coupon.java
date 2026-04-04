package com.loopers.domain.coupon;

import com.loopers.domain.BaseTimeEntity;
import com.loopers.domain.coupon.attribute.CouponType;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupons")
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login_id", nullable = false)
    private String userLoginId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CouponType type;

    @Column(name = "discount_value", nullable = false)
    private Long discountValue;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    public Coupon(String userLoginId, CouponType type, Long discountValue) {
        if (discountValue == null || discountValue <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인 값은 0보다 커야 합니다.");
        }
        if (type == CouponType.PERCENTAGE && discountValue > 100) {
            throw new CoreException(ErrorType.BAD_REQUEST, "정률 할인은 100% 이하여야 합니다.");
        }
        this.userLoginId = userLoginId;
        this.type = type;
        this.discountValue = discountValue;
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isOwnedBy(String userLoginId) {
        return this.userLoginId.equals(userLoginId);
    }

    public void use() {
        if (isUsed()) {
            throw new CoreException(ErrorType.CONFLICT, "이미 사용된 쿠폰입니다.");
        }
        this.usedAt = LocalDateTime.now();
    }

    public long calculateDiscount(long totalAmount) {
        return switch (type) {
            case FIXED_AMOUNT -> Math.min(discountValue, totalAmount);
            case PERCENTAGE -> totalAmount * discountValue / 100;
        };
    }
}
