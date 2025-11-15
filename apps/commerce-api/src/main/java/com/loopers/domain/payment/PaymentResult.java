package com.loopers.domain.payment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResult {
    public record GetPayment(Long paymentId, Long orderId, Long userId, Long amount, PaymentStatus status, PaymentMethod method) {
        public static GetPayment from(Payment payment) {
            return new GetPayment(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getAmount(),
                    payment.getStatus(),
                    payment.getMethod()
            );
        }
    }
}
