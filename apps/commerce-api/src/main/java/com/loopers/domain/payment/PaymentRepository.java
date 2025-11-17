package com.loopers.domain.payment;

import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findByOrderId(Long orderId);

    Payment save(Payment payment);

}
