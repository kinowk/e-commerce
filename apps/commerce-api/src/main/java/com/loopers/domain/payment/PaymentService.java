package com.loopers.domain.payment;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.attribute.OrderStatus;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PgClient pgClient;
    private final OrderService orderService;

    @Value("${pg.callback-url}")
    private String callbackUrl;

    @Transactional
    public PaymentResult.Detail requestPayment(PaymentCommand.Request command) {
        Payment payment = new Payment(command.orderId(), command.cardType(), command.cardNo(), command.amount());
        payment = paymentRepository.save(payment);

        String transactionId = pgClient.requestPayment(
                command.orderId(), command.cardType(), command.cardNo(), command.amount(), callbackUrl
        );

        if (transactionId != null) {
            payment.registerTransaction(transactionId);
            paymentRepository.save(payment);
            log.info("[Payment] PG 결제 요청 성공 - orderId: {}, transactionId: {}", command.orderId(), transactionId);
        } else {
            payment.fail(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            orderService.updateOrderStatus(command.orderId(), OrderStatus.FAILED);
            log.warn("[Payment] PG 결제 요청 실패 (fallback) - orderId: {}", command.orderId());
        }

        return PaymentResult.Detail.from(payment);
    }

    @Transactional
    public void handleCallback(PaymentCommand.Callback command) {
        Payment payment = paymentRepository.findByTransactionId(command.transactionId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

        PaymentStatus newStatus = resolveStatus(command.status());
        OrderStatus orderStatus;

        if (newStatus == PaymentStatus.COMPLETED) {
            payment.complete();
            orderStatus = OrderStatus.PAID;
        } else {
            payment.fail(newStatus);
            orderStatus = OrderStatus.FAILED;
        }

        paymentRepository.save(payment);
        orderService.updateOrderStatus(payment.getOrderId(), orderStatus);
        log.info("[Payment] 콜백 처리 완료 - transactionId: {}, status: {}", command.transactionId(), newStatus);
    }

    @Transactional
    public PaymentResult.Detail syncPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

        if (!payment.isPending()) {
            return PaymentResult.Detail.from(payment);
        }

        pgClient.getPaymentByOrderId(orderId).ifPresentOrElse(
                detail -> {
                    PaymentStatus newStatus = resolveStatus(detail.status());
                    if (newStatus == PaymentStatus.COMPLETED) {
                        payment.registerTransaction(detail.transactionId());
                        payment.complete();
                        orderService.updateOrderStatus(orderId, OrderStatus.PAID);
                    } else {
                        payment.fail(newStatus);
                        orderService.updateOrderStatus(orderId, OrderStatus.FAILED);
                    }
                    paymentRepository.save(payment);
                    log.info("[Payment] 동기화 완료 - orderId: {}, status: {}", orderId, newStatus);
                },
                () -> log.info("[Payment] PG에 결제 정보 없음 - orderId: {}", orderId)
        );

        return PaymentResult.Detail.from(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResult.Detail getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentResult.Detail::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));
    }

    private PaymentStatus resolveStatus(String pgStatus) {
        return switch (pgStatus.toUpperCase()) {
            case "SUCCESS", "COMPLETED" -> PaymentStatus.COMPLETED;
            case "LIMIT_EXCEEDED" -> PaymentStatus.LIMIT_EXCEEDED;
            case "INVALID_CARD" -> PaymentStatus.INVALID_CARD;
            default -> PaymentStatus.FAILED;
        };
    }
}
