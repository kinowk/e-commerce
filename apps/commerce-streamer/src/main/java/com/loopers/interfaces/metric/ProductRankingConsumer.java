package com.loopers.interfaces.metric;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.kafka.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductRankingConsumer {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String RANKING_KEY_PREFIX = "ranking:all:";
    private static final long TTL_SECONDS = 60 * 60 * 24 * 2; // 2 days

    @KafkaListener(
            topics = "${loopers.kafka.topics.ProductEvent.Ranking}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consumeProductEvents(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment) throws IOException {
        log.info("Batch Consumed ProductEvent Size: {}", messages.size());

        List<ProductRankingEvent> events = messages.stream()
                .map(message -> {
                    try {
                        KafkaMessage<ProductEventPayload> kafkaMessage = objectMapper.readValue(
                                message.value(),
                                new TypeReference<>() {}
                        );
                        return convertToRankingEvent(kafkaMessage, "LIKE");
                    } catch (IOException e) {
                        log.error("Failed to parse ProductEvent message", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        processEvents(events);
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "${loopers.kafka.topics.LikeEvent.Like}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consumeLikeEvents(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment) throws IOException {
        log.info("Batch Consumed LikeEvent Size: {}", messages.size());

        List<ProductRankingEvent> events = messages.stream()
                .map(message -> {
                    try {
                        KafkaMessage<LikeEventPayload> kafkaMessage = objectMapper.readValue(
                                message.value(),
                                new TypeReference<>() {}
                        );
                        return convertToRankingEvent(kafkaMessage, "LIKE");
                    } catch (IOException e) {
                        log.error("Failed to parse LikeEvent message", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        processEvents(events);
        acknowledgment.acknowledge();
    }

    private void processEvents(List<ProductRankingEvent> events) {
        if (events.isEmpty()) {
            return;
        }

        String key = generateKey();

        if (!redisTemplate.hasKey(key)) {
            redisTemplate.expire(key, java.time.Duration.ofSeconds(TTL_SECONDS));
        }

        events.stream()
                .collect(Collectors.groupingBy(ProductRankingEvent::productId))
                .forEach((productId, list) -> {
                    double score = list.stream()
                            .mapToDouble(this::calculateScore)
                            .sum();

                    redisTemplate.opsForZSet()
                            .incrementScore(key, productId.toString(), score);
                });

        log.info("Processed {} events for ranking", events.size());
    }

    private ProductRankingEvent convertToRankingEvent(KafkaMessage<?> kafkaMessage, String type) {
        Object payload = kafkaMessage.payload();
        Long productId = null;
        Long price = null;
        Long amount = null;

        if (payload instanceof ProductEventPayload productEvent) {
            productId = productEvent.productId();
        } else if (payload instanceof LikeEventPayload likeEvent) {
            productId = likeEvent.productId();
        }

        if (productId == null) {
            return null;
        }

        return new ProductRankingEvent(
                productId,
                type,
                price != null ? price : 0L,
                amount != null ? amount : 1L,
                kafkaMessage.publishedAt().toLocalDateTime()
        );
    }

    private static String generateKey() {
        return RANKING_KEY_PREFIX + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private double calculateScore(ProductRankingEvent event) {
        return switch (event.type()) {
            case "VIEW" -> WEIGHT_VIEW * event.amount();
            case "LIKE" -> WEIGHT_LIKE * event.amount();
            case "ORDER" -> {
                double orderScore = event.price() * event.amount();
                yield WEIGHT_ORDER * Math.log1p(orderScore);
            }
            default -> 0.0;
        };
    }

    private record ProductEventPayload(Long productId, Long likeCount) {}
    private record LikeEventPayload(Long userId, Long productId) {}

}
