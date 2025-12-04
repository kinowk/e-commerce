package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentAttemptManager {


    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResult.RecordRequest recordRequest(Long paymentId) {
        PaymentAttempt paymentAttempt = PaymentAttempt.builder()
                .paymentId(paymentId)
                .orderUid(UUID.randomUUID().toString())
                .build();
        PaymentAttempt savedPaymentAttempt = paymentRepository.save(paymentAttempt);

        return PaymentResult.RecordRequest.from(paymentAttempt);
    }

    @Transactional
    public PaymentResult.RecordRequest recordRequest(PaymentCommand.RecordResponse command) {
        PaymentAttempt paymentAttempt = PaymentAttempt.builder()
                .paymentId(command.paymentId())
                .orderUid(command.orderUid())
                .transactionKey(command.transactionKey())
                .build();
        PaymentAttempt savedPaymentAttempt = paymentRepository.save(paymentAttempt);

        return PaymentResult.RecordRequest.from(savedPaymentAttempt);
    }
}
