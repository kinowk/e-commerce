package com.loopers.domain.payment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {

    public record GetPayment(Long orderId, Long userId) {

    }

    public record Pay(Long orderId, Long userId, Long amount, PaymentMethod paymentMethod) {

    }

    public record RecordResponse(Long paymentId, String orderUid, String transactionKey) {

    }
}
