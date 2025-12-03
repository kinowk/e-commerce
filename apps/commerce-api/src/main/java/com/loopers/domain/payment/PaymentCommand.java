package com.loopers.domain.payment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {

    public record GetPayment(Long orderId, Long userId) {

    }

    public record Ready(Long orderId, Long userId, Long amount, PaymentMethod paymentMethod) {

    }

    public record Attempt(Long paymentId, String orderUid, Long amount, PaymentMethod paymentMethod) {

    }

    public record Pay(Long orderId, Long userId, Long amount, PaymentMethod paymentMethod) {

    }
}
