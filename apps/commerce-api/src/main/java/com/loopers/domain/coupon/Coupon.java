package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Getter
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "coupon_name", nullable = false)
    private String name;

    @Column(name = "ref_user_id", nullable = false)
    private Long userId;

    @Enumerated(value = EnumType.STRING)
    private DiscountPolicy discountPolicy;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "used")
    private boolean used;

    @Column(name = "used_at")
    private ZonedDateTime usedAt;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

    @Builder
    private Coupon(String name, Long userId, DiscountPolicy discountPolicy, Long amount, ZonedDateTime expiresAt) {
        this.name = name;
        this.userId = userId;
        this.discountPolicy = discountPolicy;
        this.amount = amount;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    public boolean isExpired() {
        return expiresAt != null && ZonedDateTime.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean canUse() {
        if (isExpired())
            return false;

        if (isUsed())
            return false;

        return used;
    }
}
