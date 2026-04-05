package com.loopers.domain.payment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {

    public record Request(Long orderId, String cardType, String cardNo, Long amount) {
    }

    public record Callback(String transactionId, Long orderId, String status, Long amount) {
    }
}
