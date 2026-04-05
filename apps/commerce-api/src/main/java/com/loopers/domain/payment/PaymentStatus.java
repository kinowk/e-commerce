package com.loopers.domain.payment;

public enum PaymentStatus {
    PENDING,        // 결제 요청 중 (PG 처리 대기)
    COMPLETED,      // 결제 완료
    LIMIT_EXCEEDED, // 한도 초과
    INVALID_CARD,   // 잘못된 카드
    FAILED          // 결제 실패 (PG 연동 오류)
}
