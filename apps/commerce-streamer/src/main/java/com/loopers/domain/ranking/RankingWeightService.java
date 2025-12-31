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

    public double getViewWeight() {
        return getWeight(WEIGHT_VIEW_KEY, defaultWeightView);
    }

    public double getLikeWeight() {
        return getWeight(WEIGHT_LIKE_KEY, defaultWeightLike);
    }

    public double getOrderWeight() {
        return getWeight(WEIGHT_ORDER_KEY, defaultWeightOrder);
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
}
