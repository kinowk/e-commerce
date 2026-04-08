package com.loopers.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.infrastructure.ranking.RankingRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CatalogEventProcessor extends AbstractEventProcessor {

    private final ProductMetricsService productMetricsService;
    private final RankingRedisRepository rankingRedisRepository;

    public CatalogEventProcessor(EventIdempotencyService idempotencyService,
                                 ObjectMapper objectMapper,
                                 ProductMetricsService productMetricsService,
                                 RankingRedisRepository rankingRedisRepository) {
        super(idempotencyService, objectMapper);
        this.productMetricsService = productMetricsService;
        this.rankingRedisRepository = rankingRedisRepository;
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
                    rankingRedisRepository.incrementLikeScore(productId);
                } else {
                    productMetricsService.decrementLikeCount(productId);
                    rankingRedisRepository.decrementLikeScore(productId);
                }
            }
            default -> log.warn("[CatalogConsumer] 알 수 없는 이벤트 타입 - eventType: {}", eventType);
        }
    }
}
