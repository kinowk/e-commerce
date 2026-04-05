package com.loopers.domain.payment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResult {

    public record Detail(Long paymentId, Long orderId, String transactionId,
                         String cardType, Long amount, PaymentStatus status) {
        public static Detail from(Payment payment) {
            return new Detail(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getTransactionId(),
                    payment.getCardType(),
                    payment.getAmount(),
                    payment.getStatus()
            );
        }
    }
}
