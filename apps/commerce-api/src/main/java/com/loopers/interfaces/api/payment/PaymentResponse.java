package com.loopers.interfaces.api.payment;

import com.loopers.domain.payment.PaymentResult;
import com.loopers.domain.payment.PaymentStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResponse {

    public record Detail(Long paymentId, Long orderId, String transactionId,
                         String cardType, Long amount, PaymentStatus status) {
        public static Detail from(PaymentResult.Detail result) {
            return new Detail(
                    result.paymentId(),
                    result.orderId(),
                    result.transactionId(),
                    result.cardType(),
                    result.amount(),
                    result.status()
            );
        }
    }
}
