package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentResult;
import com.loopers.domain.payment.PaymentStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentOutput {
    public record Pay(Long paymentId, PaymentStatus paymentStatus) {
        public static Pay from(PaymentResult.Pay result) {
            return new Pay(
                    result.paymentId(),
                    result.paymentStatus()
            );
        }
    }
}
