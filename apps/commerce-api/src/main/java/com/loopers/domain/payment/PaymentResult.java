package com.loopers.domain.payment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResult {

    public record GetPayment(Long paymentId, Long orderId, Long userId, Long amount, PaymentStatus status, PaymentMethod method) {
        public static GetPayment from(Payment payment) {
            return new GetPayment(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getAmount(),
                    payment.getStatus(),
                    payment.getMethod()
            );
        }
    }

    public record Pay(Long paymentId, PaymentStatus paymentStatus) {
        public static Pay from(Payment payment) {
            return new Pay(
                    payment.getId(),
                    payment.getStatus()
            );
        }
    }

    public record Ready(Long paymentId, PaymentMethod method, PaymentStatus status) {
        public static Ready from(Payment payment) {
            return new Ready(
                    payment.getId(),
                    payment.getMethod(),
                    payment.getStatus()
            );
        }
    }

    public record Fail(Long paymentId, PaymentStatus status) {
        public static Fail from(Payment payment) {
            return new Fail(
                    payment.getId(),
                    payment.getStatus()
            );
        }
    }
}
