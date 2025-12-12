package com.loopers.domain.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findByOrderId(Long orderId);

    Payment save(Payment payment);

    List<Payment> findReadyPayments(PaymentMethod method);

    Optional<Payment> findById(Long paymentId);
}
