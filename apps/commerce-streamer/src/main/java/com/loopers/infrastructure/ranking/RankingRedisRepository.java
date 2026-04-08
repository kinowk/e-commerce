package com.loopers.infrastructure.ranking;

import com.loopers.config.redis.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Repository
public class RankingRedisRepository {

    private static final String KEY_PREFIX = "ranking:all:";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Duration TTL = Duration.ofDays(2);

    private static final double WEIGHT_VIEW = 0.1;
    private static final double WEIGHT_LIKE = 0.2;
    private static final double WEIGHT_ORDER = 0.6;

    private final RedisTemplate<String, String> redisTemplate;

    public RankingRedisRepository(
            @Qualifier(RedisConfig.REDIS_TEMPLATE_MASTER) RedisTemplate<String, String> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    public void incrementViewScore(Long productId) {
        incrementScore(productId, WEIGHT_VIEW * 1);
    }

    public void incrementLikeScore(Long productId) {
        incrementScore(productId, WEIGHT_LIKE * 1);
    }

    public void decrementLikeScore(Long productId) {
        incrementScore(productId, -(WEIGHT_LIKE * 1));
    }

    public void incrementOrderScore(Long productId, long amount) {
        double score = WEIGHT_ORDER * Math.log1p(amount);
        incrementScore(productId, score);
    }

    private void incrementScore(Long productId, double score) {
        String key = buildKey(LocalDate.now());
        String member = String.valueOf(productId);
        redisTemplate.opsForZSet().incrementScore(key, member, score);

        Boolean hasExpire = redisTemplate.getExpire(key) != null && redisTemplate.getExpire(key) > 0;
        if (hasExpire == null || !hasExpire) {
            redisTemplate.expire(key, TTL);
        }
    }

    public static String buildKey(LocalDate date) {
        return KEY_PREFIX + date.format(DATE_FORMAT);
    }
}
