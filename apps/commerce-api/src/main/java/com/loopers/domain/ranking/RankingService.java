package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String RANKING_KEY_PREFIX = "ranking:all:";

    public List<RankingResult> getRankings(String date, int page, int size) {
        String key = RANKING_KEY_PREFIX + date;

        long start = (long) (page - 1) * size;
        long end = start + size - 1;

        Set<ZSetOperations.TypedTuple<String>> result =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(key, start, end);

        if (result == null || result.isEmpty())
            return List.of();

        AtomicLong rank = new AtomicLong(start + 1);

        return result.stream()
                .map(tuple -> {
                    Long productId = Long.parseLong(tuple.getValue());
                    Double score = tuple.getScore();
                    Long currentRank = rank.getAndIncrement();
                    return RankingResult.of(currentRank, productId, score);
                })
                .collect(Collectors.toList());
    }

    public Long getProductRank(String date, Long productId) {
        String key = RANKING_KEY_PREFIX + date;

        Long rank = redisTemplate.opsForZSet()
                .reverseRank(key, productId.toString());

        return (rank == null) ? null : rank + 1;
    }
}
