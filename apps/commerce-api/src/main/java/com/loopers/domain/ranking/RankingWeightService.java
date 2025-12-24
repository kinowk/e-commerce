package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingWeightService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String WEIGHT_KEY_PREFIX = "ranking:weight:";
    private static final String WEIGHT_VIEW_KEY = WEIGHT_KEY_PREFIX + "view";
    private static final String WEIGHT_LIKE_KEY = WEIGHT_KEY_PREFIX + "like";
    private static final String WEIGHT_ORDER_KEY = WEIGHT_KEY_PREFIX + "order";

    @Value("${loopers.ranking.weight.view:0.1}")
    private double defaultWeightView;

    @Value("${loopers.ranking.weight.like:0.2}")
    private double defaultWeightLike;

    @Value("${loopers.ranking.weight.order:0.6}")
    private double defaultWeightOrder;

    public void updateWeight(RankingWeightCommand.UpdateWeight command) {
        String key = getKeyByType(command.type());

        if (command.weight() < 0) {
            throw new IllegalArgumentException("가중치(Weight) 는 0보다 크거나 같아야합니다.");
        }

        redisTemplate.opsForValue().set(key, String.valueOf(command.weight()));
        log.info("Updated {} weight to {}", command.type(), command.weight());
    }

    public void resetWeight(RankingWeightCommand.ResetWeight command) {
        double defaultValue = getDefaultByType(command.type());
        String key = getKeyByType(command.type());

        redisTemplate.opsForValue().set(key, String.valueOf(defaultValue));
        log.info("Reset {} weight to default: {}", command.type(), defaultValue);
    }

    public RankingWeightResult.GetWeights getWeights() {
        double view = getWeight(WEIGHT_VIEW_KEY, defaultWeightView);
        double like = getWeight(WEIGHT_LIKE_KEY, defaultWeightLike);
        double order = getWeight(WEIGHT_ORDER_KEY, defaultWeightOrder);

        return new RankingWeightResult.GetWeights(view, like, order);
    }

    private double getWeight(String key, double defaultValue) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (Exception e) {
            log.warn("Failed to get weight from Redis: {}, using default: {}", key, defaultValue, e);
            return defaultValue;
        }
    }

    private String getKeyByType(String type) {
        return switch (type.toUpperCase()) {
            case "VIEW" -> WEIGHT_VIEW_KEY;
            case "LIKE" -> WEIGHT_LIKE_KEY;
            case "ORDER" -> WEIGHT_ORDER_KEY;
            default -> throw new IllegalArgumentException("Unknown weight type: " + type);
        };
    }

    private double getDefaultByType(String type) {
        return switch (type.toUpperCase()) {
            case "VIEW" -> defaultWeightView;
            case "LIKE" -> defaultWeightLike;
            case "ORDER" -> defaultWeightOrder;
            default -> throw new IllegalArgumentException("Unknown weight type: " + type);
        };
    }
}
