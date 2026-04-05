package com.loopers.infrastructure.payment;

record PgPaymentRequest(
        String orderId,
        String cardType,
        String cardNo,
        String amount,
        String callbackUrl
) {
}
