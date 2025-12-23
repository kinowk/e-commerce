package com.loopers.interfaces.metric;

import com.loopers.config.kafka.KafkaConfig;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductRankingConsumer {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String RANKING_KEY_PREFIX = "ranking:all:";
    private static final long TTL = 60 * 60 * 24 * 2; // 2 days

    private static final double WEIGHT_VIEW = 0.1;
    private static final double WEIGHT_LIKE = 0.2;
    private static final double WEIGHT_ORDER = 0.6;

    @KafkaListener(
            topics = "${loopers.kafka.topics.ProductEvent.Ranking}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consume(List<ProductRankingEvent> events) {
        log.info("Batch Consumed Size: {}", events.size());

        String key = generateKey();

        events.stream()
                .collect(Collectors.groupingBy(ProductRankingEvent::productId))
                .forEach((productId, list) -> {
                    double score = list.stream()
                            .mapToDouble(this::calculateScore)
                            .sum();

                    redisTemplate.opsForZSet()
                            .incrementScore(key, productId.toString(), score);
                });
    }

    private static String generateKey() {
        return RANKING_KEY_PREFIX + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private double calculateScore(ProductRankingEvent event) {
        return switch (event.type()) {
            case "VIEW" -> WEIGHT_VIEW;
            case "LIKE" -> WEIGHT_LIKE;
            case "ORDER" -> WEIGHT_ORDER * (event.price() * event.amount());
            default -> 0.0;
        };
    }
}
