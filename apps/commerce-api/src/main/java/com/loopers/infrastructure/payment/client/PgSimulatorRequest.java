package com.loopers.infrastructure.payment.client;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentCardType;

public class PgSimulatorRequest {

    public record RequestTransaction(Long orderId, Long amount, PaymentCardType paymentCardType, String cardNo, String callbackUrl) {
        public static RequestTransaction of(Payment payment, String callbackUrl) {
            return new RequestTransaction(
                    payment.getOrderId(),
                    payment.getAmount(),
                    payment.getPaymentCardType(),
                    payment.getCardNo(),
                    callbackUrl
            );
        }
    }
}
