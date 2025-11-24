package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentMethod;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentInput {
    public record Pay(Long orderId, Long userId, PaymentMethod paymentMethod) {
    }
}
