package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Optional<PaymentResult.GetPayment> getPayment(PaymentCommand.GetPayment command) {
        return paymentRepository.findByOrderId(command.orderId())
                .filter(payment -> Objects.equals(payment.getUserId(), command.userId()))
                .map(PaymentResult.GetPayment::from);

    }
}
