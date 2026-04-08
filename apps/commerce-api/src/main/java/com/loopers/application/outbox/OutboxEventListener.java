package com.loopers.application.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.like.event.LikeToggledEvent;
import com.loopers.domain.order.OrderItem;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.order.event.OrderCreatedEvent;
import com.loopers.domain.outbox.KafkaEventMessage;
import com.loopers.domain.outbox.OutboxEvent;
import com.loopers.domain.outbox.OutboxRepository;
import com.loopers.domain.payment.event.PaymentResultEvent;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class OutboxEventListener {

    private final OutboxRepository outboxRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final String catalogTopic;
    private final String orderTopic;

    public OutboxEventListener(
            OutboxRepository outboxRepository,
            OrderRepository orderRepository,
            ObjectMapper objectMapper,
            @Value("${kafka.topics.catalog-events:catalog-events-v1}") String catalogTopic,
            @Value("${kafka.topics.order-events:order-events-v1}") String orderTopic
    ) {
        this.outboxRepository = outboxRepository;
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
        this.catalogTopic = catalogTopic;
        this.orderTopic = orderTopic;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCreated(OrderCreatedEvent event) {
        List<Map<String, Object>> items = event.items().stream()
                .map(i -> Map.<String, Object>of(
                        "productId", i.productId(),
                        "quantity", i.quantity(),
                        "unitPrice", i.unitPrice()
                ))
                .toList();
        saveOutboxEvent(
                "ORDER", event.orderId(), "ORDER_CREATED", orderTopic,
                String.valueOf(event.orderId()),
                Map.of(
                        "orderId", event.orderId(),
                        "userId", event.userId(),
                        "finalAmount", event.finalAmount(),
                        "items", items
                )
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePaymentResult(PaymentResultEvent event) {
        List<OrderItem> orderItems = orderRepository.findItemsByOrderId(event.orderId());
        List<Map<String, Object>> items = orderItems.stream()
                .map(i -> Map.<String, Object>of(
                        "productId", i.getProductId(),
                        "quantity", i.getQuantity(),
                        "unitPrice", i.getUnitPrice()
                ))
                .toList();
        saveOutboxEvent(
                "ORDER", event.orderId(), "PAYMENT_RESULT", orderTopic,
                String.valueOf(event.orderId()),
                Map.of(
                        "orderId", event.orderId(),
                        "paymentId", event.paymentId(),
                        "status", event.status().name(),
                        "items", items
                )
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleLikeToggled(LikeToggledEvent event) {
        saveOutboxEvent(
                "PRODUCT", event.productId(), "LIKE_TOGGLED", catalogTopic,
                String.valueOf(event.productId()),
                Map.of(
                        "userId", event.userId(),
                        "productId", event.productId(),
                        "added", event.added()
                )
        );
    }

    private void saveOutboxEvent(String aggregateType, Long aggregateId, String eventType,
                                 String topic, String partitionKey, Map<String, Object> payload) {
        try {
            KafkaEventMessage message = new KafkaEventMessage(
                    UUID.randomUUID().toString(), eventType, aggregateType,
                    aggregateId, payload, ZonedDateTime.now()
            );
            String json = objectMapper.writeValueAsString(message);
            outboxRepository.save(new OutboxEvent(
                    aggregateType, aggregateId, eventType, topic, partitionKey, json
            ));
            log.debug("[Outbox] 이벤트 저장 - type: {}, aggregateId: {}", eventType, aggregateId);
        } catch (JsonProcessingException e) {
            throw new CoreException(ErrorType.INTERNAL_ERROR,
                    "[Outbox] 이벤트 직렬화 실패 - type: " + eventType + ", aggregateId: " + aggregateId);
        }
    }
}
