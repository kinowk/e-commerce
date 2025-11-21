package com.loopers.domain.coupon;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DiscountPolicy {

    FIXED("고정 금액 할인 (원)"),
    PERCENT("백분율 할인 (%)");

    private final String description;
}
