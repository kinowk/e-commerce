package com.loopers.domain.payment;

import com.loopers.domain.payment.event.PaymentEvent;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public PaymentResult.GetPayment getPayment(PaymentCommand.GetPayment command) {
        return paymentRepository.findByOrderId(command.orderId())
                .filter(payment -> Objects.equals(payment.getUserId(), command.userId()))
                .map(PaymentResult.GetPayment::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }

    @Transactional
    public PaymentResult.Ready ready(PaymentCommand.Ready command) {
        Payment payment = Payment.builder()
                .status(PaymentStatus.PENDING)
                .method(command.method())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResult.Ready.from(savedPayment);
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

        applicationEventPublisher.publishEvent(PaymentEvent.Paid.from(savedPayment));

        return PaymentResult.Pay.from(savedPayment);
    }

    public void recordAsSuccess(PaymentCommand.RecordAsSuccess command) {

    }

    public void recordAsFailed(PaymentCommand.RecordAsFailed command) {
    }

    @Transactional(readOnly = true)
    public PaymentResult.GetPayment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(PaymentResult.GetPayment::from)
                .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST));
    }

    @Transactional
    public PaymentResult.Fail fail(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        payment.fail();
        paymentRepository.save(payment);

        applicationEventPublisher.publishEvent(PaymentEvent.Failed.from(payment));

        return PaymentResult.Fail.from(payment);
    }
}
