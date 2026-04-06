package com.loopers.domain.payment.event;

import com.loopers.domain.payment.PaymentStatus;

public record PaymentResultEvent(
        Long orderId,
        Long paymentId,
        PaymentStatus status
) {
}
