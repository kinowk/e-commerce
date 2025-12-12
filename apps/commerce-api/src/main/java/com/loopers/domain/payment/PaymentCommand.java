package com.loopers.domain.payment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {

    public record GetPayment(Long orderId, Long userId) {

    }

    public record Pay(Long orderId, Long userId, Long amount, PaymentMethod paymentMethod) {

    }

    public record Ready(Long paymentId, PaymentMethod method, PaymentStatus status) {

    }

    public record RecordAsSuccess(String transactionKey, Long orderId, Long paymentId) {

    }

    public record RecordAsFailed(String transactionKey, String description, Long orderId, Long paymentId) {

    }
}
