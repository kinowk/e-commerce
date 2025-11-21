package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResult.GetPayment getPayment(PaymentCommand.GetPayment command) {
        return paymentRepository.findByOrderId(command.orderId())
                .filter(payment -> Objects.equals(payment.getUserId(), command.userId()))
                .map(PaymentResult.GetPayment::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }

    @Transactional
    public PaymentResult.Pay pay(PaymentCommand.Pay command) {
        Payment payment = Payment.builder()
                .orderId(command.orderId())
                .userId(command.userId())
                .amount(command.amount())
                .status(PaymentStatus.COMPLETED)
                .method(command.paymentMethod())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResult.Pay.from(savedPayment);
    }
}
