package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingRepository;
import com.loopers.domain.ranking.RankingResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class RankingRedisRepositoryImpl implements RankingRepository {

    private static final String KEY_PREFIX = "ranking:all:";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final RedisTemplate<String, String> redisTemplate;

    public RankingRedisRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<RankingResult.Entry> getRankingPage(LocalDate date, int offset, int size) {
        String key = buildKey(date);
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, offset, (long) offset + size - 1);

        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }

        List<RankingResult.Entry> entries = new ArrayList<>();
        long rank = offset + 1;
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            entries.add(new RankingResult.Entry(rank++, Long.parseLong(tuple.getValue()), tuple.getScore()));
        }
        return entries;
    }

    @Override
    public Long getRank(LocalDate date, Long productId) {
        String key = buildKey(date);
        Long rank = redisTemplate.opsForZSet().reverseRank(key, String.valueOf(productId));
        return rank != null ? rank + 1 : null;
    }

    @Override
    public Double getScore(LocalDate date, Long productId) {
        String key = buildKey(date);
        return redisTemplate.opsForZSet().score(key, String.valueOf(productId));
    }

    private String buildKey(LocalDate date) {
        return KEY_PREFIX + date.format(DATE_FORMAT);
    }
}
