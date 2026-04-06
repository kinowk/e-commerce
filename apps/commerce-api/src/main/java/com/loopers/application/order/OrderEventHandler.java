package com.loopers.application.order;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.attribute.OrderStatus;
import com.loopers.domain.order.event.OrderCreatedEvent;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.domain.payment.event.PaymentResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final PaymentService paymentService;
    private final OrderService orderService;

    /**
     * 주문 생성 후 PG 결제 요청
     */
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async("eventExecutor")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("[OrderEventHandler] 주문 생성 이벤트 수신 - orderId: {}", event.orderId());
        try {
            if (event.finalAmount() > 0) {
                paymentService.requestPayment(new PaymentCommand.Request(
                        event.orderId(), event.cardType(), event.cardNo(), event.finalAmount()
                ));
            } else {
                // 전액 할인 시 결제 없이 바로 완료
                orderService.updateOrderStatus(event.orderId(), OrderStatus.PAID);
                log.info("[OrderEventHandler] 전액 할인 주문 완료 처리 - orderId: {}", event.orderId());
            }
        } catch (Exception e) {
            log.error("[OrderEventHandler] 결제 요청 실패 - orderId: {}, reason: {}", event.orderId(), e.getMessage(), e);
            try {
                orderService.updateOrderStatus(event.orderId(), OrderStatus.FAILED);
            } catch (Exception ex) {
                log.error("[OrderEventHandler] 주문 상태 FAILED 변경 실패 - orderId: {}", event.orderId(), ex);
            }
        }
    }

    /**
     * PG 결제 결과 수신 후 주문 상태 업데이트
     */
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async("eventExecutor")
    public void handlePaymentResult(PaymentResultEvent event) {
        log.info("[OrderEventHandler] 결제 결과 이벤트 수신 - orderId: {}, status: {}", event.orderId(), event.status());
        try {
            OrderStatus orderStatus = event.status() == PaymentStatus.COMPLETED
                    ? OrderStatus.PAID
                    : OrderStatus.FAILED;
            orderService.updateOrderStatus(event.orderId(), orderStatus);
        } catch (Exception e) {
            log.error("[OrderEventHandler] 주문 상태 업데이트 실패 - orderId: {}, reason: {}", event.orderId(), e.getMessage(), e);
        }
    }
}
