package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AttemptStatus {
    PENDING("대기"),
    SUCCESS("성공"),
    FAILED("실패");

    private final String description;
}
