package com.loopers.domain.payment;

import java.util.Optional;

public interface PgClient {

    /**
     * PG에 결제 요청을 보냅니다. 실패 시 null을 반환합니다 (fallback).
     */
    String requestPayment(Long orderId, String cardType, String cardNo, Long amount, String callbackUrl);

    /**
     * PG에서 주문 ID로 결제 상태를 조회합니다.
     */
    Optional<PgPaymentDetail> getPaymentByOrderId(Long orderId);

    record PgPaymentDetail(String transactionId, String status, Long amount) {
    }
}
