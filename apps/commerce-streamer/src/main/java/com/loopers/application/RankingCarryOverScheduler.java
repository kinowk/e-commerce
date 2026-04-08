package com.loopers.application;

import com.loopers.config.redis.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class RankingCarryOverScheduler {

    private static final String KEY_PREFIX = "ranking:all:";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Duration TTL = Duration.ofDays(2);
    private static final double CARRY_OVER_WEIGHT = 0.1;

    private final RedisTemplate<String, String> redisTemplate;

    public RankingCarryOverScheduler(
            @Qualifier(RedisConfig.REDIS_TEMPLATE_MASTER) RedisTemplate<String, String> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void carryOver() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        String yesterdayKey = KEY_PREFIX + yesterday.format(DATE_FORMAT);
        String todayKey = KEY_PREFIX + today.format(DATE_FORMAT);

        Boolean exists = redisTemplate.hasKey(yesterdayKey);
        if (exists == null || !exists) {
            log.info("[CarryOver] 전일 랭킹 데이터 없음 - key: {}", yesterdayKey);
            return;
        }

        // 전일 점수의 10%를 오늘 키로 복사 (Score Carry-Over)
        var tuples = redisTemplate.opsForZSet().rangeWithScores(yesterdayKey, 0, -1);
        if (tuples != null && !tuples.isEmpty()) {
            for (var tuple : tuples) {
                double carryScore = tuple.getScore() * CARRY_OVER_WEIGHT;
                redisTemplate.opsForZSet().incrementScore(todayKey, tuple.getValue(), carryScore);
            }
        }

        redisTemplate.expire(todayKey, TTL);
        log.info("[CarryOver] 스코어 이전 완료 - from: {}, to: {}, weight: {}", yesterdayKey, todayKey, CARRY_OVER_WEIGHT);
    }
}
