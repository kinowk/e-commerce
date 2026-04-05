package com.loopers.interfaces.api.payment;

import com.loopers.domain.payment.PaymentCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentRequest {

    public record Callback(String transactionId, Long orderId, String status, Long amount) {
        public PaymentCommand.Callback toCommand() {
            return new PaymentCommand.Callback(transactionId, orderId, status, amount);
        }
    }
}
