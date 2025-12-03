package com.loopers.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

    POINT("포인트"),
    CARD("카드"),
    VIRTUAL_ACCOUNT("가상계좌"),
    NAVER_PAY("네이버페이"),
    KAKAO_PAY("카카오페이"),
    NICE_PAY("나이스페이"),
    TOSS_PAY("토스페이");

    private final String description;
}
