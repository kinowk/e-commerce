package com.loopers.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    COMPLETED("결제 완료"),
    CANCELED("결제 취소");

    private final String description;
}
