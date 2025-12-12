package com.loopers.interfaces.listener.payment;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.event.PaymentEvent;
import com.support.annotation.Inboxing;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderService orderService;
    private final PaymentFacade paymentFacade;
    private final PaymentService paymentService;

    @Async
    @Inboxing(idempotent = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void pay(PaymentEvent.Paid event) {
        paymentFacade.pay(event.paymentId());
    }

    @Async
    @EventListener
    public void recordTransactionAsSuccess(PaymentEvent.Success event) {
        PaymentCommand.RecordAsSuccess command = new PaymentCommand.RecordAsSuccess(
                event.transactionKey(),
                event.orderId(),
                event.paymentId()
        );

        paymentService.recordAsSuccess(command);
    }

    @Async
    @EventListener
    public void recordTransactionAsFailed(PaymentEvent.Failed event) {
        PaymentCommand.RecordAsFailed command = new PaymentCommand.RecordAsFailed(
                event.eventId(),
                event.eventName(),
                event.orderId(),
                event.paymentId()
        );

        paymentService.recordAsFailed(command);
    }

    @Inboxing(idempotent = true)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void cancelOrder(PaymentEvent.Failed event) {
        orderService.cancel(event.orderId());
    }
}
