package com.loopers.application;

import com.loopers.domain.like.event.LikeToggledEvent;
import com.loopers.domain.order.event.OrderCreatedEvent;
import com.loopers.domain.payment.event.PaymentResultEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

/**
 * 유저 행동 로깅 핸들러 — 이벤트 기반으로 서버 레벨 행동 추적
 */
@Slf4j
@Component
public class ActivityLogEventHandler {

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async("eventExecutor")
    public void logOrderCreated(OrderCreatedEvent event) {
        log.info("[ActivityLog] ORDER_CREATED userId={} orderId={} finalAmount={}",
                event.userId(), event.orderId(), event.finalAmount());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async("eventExecutor")
    public void logPaymentResult(PaymentResultEvent event) {
        log.info("[ActivityLog] PAYMENT_RESULT orderId={} paymentId={} status={}",
                event.orderId(), event.paymentId(), event.status());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async("eventExecutor")
    public void logLikeToggled(LikeToggledEvent event) {
        String action = event.added() ? "LIKE_ADDED" : "LIKE_REMOVED";
        log.info("[ActivityLog] {} userId={} productId={}",
                action, event.userId(), event.productId());
    }
}
