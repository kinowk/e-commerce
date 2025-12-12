package com.loopers.domain.payment.event;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.saga.event.SagaEvent;

import java.util.UUID;

public record PaymentEvent() {

    public record Ready(
            String eventId,
            String eventName,
            Long paymentId,
            Long orderId,
            Long userId,
            PaymentMethod paymentMethod,
            Long amount
    ) implements SagaEvent {
        public static Ready from(Payment payment) {
            return new Ready(
                    UUID.randomUUID().toString(),
                    "Payment.Ready",
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getMethod(),
                    payment.getAmount()
            );
        }
    }

    public record Paid(
            String eventId,
            String eventName,
            Long paymentId,
            Long orderId
    ) implements SagaEvent {
        public static Paid from(Payment payment) {
            return new Paid(
                    UUID.randomUUID().toString(),
                    "Payment.Paid",
                    payment.getId(),
                    payment.getOrderId()
            );
        }
    }

    public record Success(
            String transactionKey,
            Long orderId,
            Long paymentId
    ) {
    }

    public record Failed(
            String eventId,
            String eventName,
            Long paymentId,
            Long orderId
    ) implements SagaEvent {
        public static Failed from(Payment payment) {
            return new Failed(
                    UUID.randomUUID().toString(),
                    "Payment.Failed",
                    payment.getId(),
                    payment.getOrderId()
            );
        }
    }
}
