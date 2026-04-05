package com.loopers.domain.payment;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.attribute.OrderStatus;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PgClient pgClient;
    private final OrderService orderService;
    private final String callbackUrl;
    private final TransactionTemplate transactionTemplate;

    public PaymentService(
            PaymentRepository paymentRepository,
            PgClient pgClient,
            OrderService orderService,
            org.springframework.transaction.PlatformTransactionManager transactionManager,
            @org.springframework.beans.factory.annotation.Value("${pg.callback-url}") String callbackUrl
    ) {
        this.paymentRepository = paymentRepository;
        this.pgClient = pgClient;
        this.orderService = orderService;
        this.callbackUrl = callbackUrl;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * 결제 요청: DB 저장 → PG 호출(트랜잭션 외부) → 결과 반영
     */
    public PaymentResult.Detail requestPayment(PaymentCommand.Request command) {
        // Phase 1: PENDING 결제 저장
        Payment savedPayment = transactionTemplate.execute(status -> {
            Payment payment = new Payment(command.orderId(), command.cardType(), command.cardNo(), command.amount());
            return paymentRepository.save(payment);
        });

        // Phase 2: PG 호출 (트랜잭션 외부 — DB 커넥션 미점유)
        String transactionId = pgClient.requestPayment(
                command.orderId(), command.cardType(), command.cardNo(), command.amount(), callbackUrl
        );

        // Phase 3: PG 응답 기반으로 결제 상태 갱신
        Long paymentId = savedPayment.getId();
        return transactionTemplate.execute(status -> {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

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
        });
    }

    @Transactional
    public void handleCallback(PaymentCommand.Callback command) {
        Payment payment = paymentRepository.findByTransactionId(command.transactionId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

        if (!payment.isPending()) {
            log.info("[Payment] 이미 처리된 결제 - transactionId: {}, status: {}", command.transactionId(), payment.getStatus());
            return;
        }

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

    /**
     * 결제 동기화: PG 조회(트랜잭션 외부) → 결과 반영
     */
    public PaymentResult.Detail syncPayment(Long orderId) {
        Payment payment = transactionTemplate.execute(status ->
                paymentRepository.findByOrderId(orderId)
                        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."))
        );

        if (!payment.isPending()) {
            return PaymentResult.Detail.from(payment);
        }

        // PG 조회 (트랜잭션 외부)
        var pgDetail = pgClient.getPaymentByOrderId(orderId);

        if (pgDetail.isEmpty()) {
            log.info("[Payment] PG에 결제 정보 없음 - orderId: {}", orderId);
            return PaymentResult.Detail.from(payment);
        }

        Long paymentId = payment.getId();
        var detail = pgDetail.get();
        return transactionTemplate.execute(status -> {
            Payment p = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

            PaymentStatus newStatus = resolveStatus(detail.status());
            if (newStatus == PaymentStatus.COMPLETED) {
                p.registerTransaction(detail.transactionId());
                p.complete();
                orderService.updateOrderStatus(orderId, OrderStatus.PAID);
            } else {
                p.fail(newStatus);
                orderService.updateOrderStatus(orderId, OrderStatus.FAILED);
            }
            paymentRepository.save(p);
            log.info("[Payment] 동기화 완료 - orderId: {}, status: {}", orderId, newStatus);
            return PaymentResult.Detail.from(p);
        });
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
