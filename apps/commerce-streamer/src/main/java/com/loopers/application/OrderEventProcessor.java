package com.loopers.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.infrastructure.ranking.RankingRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderEventProcessor extends AbstractEventProcessor {

    private final RankingRedisRepository rankingRedisRepository;
    private final ProductMetricsService productMetricsService;

    public OrderEventProcessor(EventIdempotencyService idempotencyService,
                               ObjectMapper objectMapper,
                               RankingRedisRepository rankingRedisRepository,
                               ProductMetricsService productMetricsService) {
        super(idempotencyService, objectMapper);
        this.rankingRedisRepository = rankingRedisRepository;
        this.productMetricsService = productMetricsService;
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
                log.info("[OrderConsumer] 주문 생성 수신 - orderId: {}", orderId);
            }
            case "PAYMENT_RESULT" -> {
                Long orderId = payload.get("orderId").asLong();
                String status = payload.get("status").asText();
                if ("SUCCESS".equals(status)) {
                    JsonNode items = payload.get("items");
                    if (items != null && items.isArray()) {
                        for (JsonNode item : items) {
                            Long productId = item.get("productId").asLong();
                            Long quantity = item.get("quantity").asLong();
                            Long unitPrice = item.get("unitPrice").asLong();
                            long amount = unitPrice * quantity;
                            rankingRedisRepository.incrementOrderScore(productId, amount);
                            productMetricsService.addSalesCount(productId, quantity);
                        }
                    }
                    log.info("[OrderConsumer] 결제 성공 집계 완료 - orderId: {}", orderId);
                } else {
                    log.info("[OrderConsumer] 결제 실패 - orderId: {}, status: {}", orderId, status);
                }
            }
            default -> log.warn("[OrderConsumer] 알 수 없는 이벤트 타입 - eventType: {}", eventType);
        }
    }
}
