package com.loopers.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

    POINT("포인트"),
    CARD("카드"),
    VIRTUAL_ACCOUNT("가상계좌");

    private final String description;
}
