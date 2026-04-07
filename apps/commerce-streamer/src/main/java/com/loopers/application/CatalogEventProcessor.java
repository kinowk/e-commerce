package com.loopers.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CatalogEventProcessor extends AbstractEventProcessor {

    private final ProductMetricsService productMetricsService;

    public CatalogEventProcessor(EventIdempotencyService idempotencyService,
                                 ObjectMapper objectMapper,
                                 ProductMetricsService productMetricsService) {
        super(idempotencyService, objectMapper);
        this.productMetricsService = productMetricsService;
    }

    @Override
    protected String consumerName() {
        return "CatalogConsumer";
    }

    @Override
    protected void handleEvent(String eventType, JsonNode payload) {
        switch (eventType) {
            case "LIKE_TOGGLED" -> {
                Long productId = payload.get("productId").asLong();
                boolean added = payload.get("added").asBoolean();
                if (added) {
                    productMetricsService.incrementLikeCount(productId);
                } else {
                    productMetricsService.decrementLikeCount(productId);
                }
            }
            default -> log.warn("[CatalogConsumer] 알 수 없는 이벤트 타입 - eventType: {}", eventType);
        }
    }
}
