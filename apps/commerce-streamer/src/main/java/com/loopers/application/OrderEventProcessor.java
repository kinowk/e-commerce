package com.loopers.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderEventProcessor extends AbstractEventProcessor {

    public OrderEventProcessor(EventIdempotencyService idempotencyService,
                               ObjectMapper objectMapper) {
        super(idempotencyService, objectMapper);
    }

    @Override
    protected String consumerName() {
        return "OrderConsumer";
    }

    @Override
    protected void handleEvent(String eventType, JsonNode payload) {
        switch (eventType) {
            case "ORDER_CREATED" -> {
                Long orderId = payload.get("orderId").asLong();
                Long userId = payload.get("userId").asLong();
                Long finalAmount = payload.get("finalAmount").asLong();
                log.info("[OrderConsumer] 주문 생성 집계 - orderId: {}, userId: {}, finalAmount: {}",
                        orderId, userId, finalAmount);
            }
            case "PAYMENT_RESULT" -> {
                Long orderId = payload.get("orderId").asLong();
                String status = payload.get("status").asText();
                log.info("[OrderConsumer] 결제 결과 집계 - orderId: {}, status: {}", orderId, status);
            }
            default -> log.warn("[OrderConsumer] 알 수 없는 이벤트 타입 - eventType: {}", eventType);
        }
    }
}
