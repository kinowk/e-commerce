package com.loopers.infrastructure.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.product.ProductCacheRepository;
import com.loopers.domain.product.ProductResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductCacheRepositoryImpl implements ProductCacheRepository {

    private static final String DETAIL_KEY_PREFIX = "product:detail:";
    private static final String LIST_KEY_PREFIX = "product:list:";
    private static final Duration DETAIL_TTL = Duration.ofMinutes(10);
    private static final Duration LIST_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<ProductResult.Detail> getDetail(Long productId) {
        String key = DETAIL_KEY_PREFIX + productId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, ProductResult.Detail.class));
        } catch (JsonProcessingException e) {
            log.warn("product detail cache deserialization failed: productId={}", productId, e);
            return Optional.empty();
        }
    }

    @Override
    public void putDetail(Long productId, ProductResult.Detail detail) {
        String key = DETAIL_KEY_PREFIX + productId;
        try {
            String json = objectMapper.writeValueAsString(detail);
            redisTemplate.opsForValue().set(key, json, DETAIL_TTL);
        } catch (JsonProcessingException e) {
            log.warn("product detail cache serialization failed: productId={}", productId, e);
        }
    }

    @Override
    public void evictDetail(Long productId) {
        redisTemplate.delete(DETAIL_KEY_PREFIX + productId);
    }

    @Override
    public Optional<ProductResult.List> getList(String cacheKey) {
        String key = LIST_KEY_PREFIX + cacheKey;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, ProductResult.List.class));
        } catch (JsonProcessingException e) {
            log.warn("product list cache deserialization failed: key={}", cacheKey, e);
            return Optional.empty();
        }
    }

    @Override
    public void putList(String cacheKey, ProductResult.List list) {
        String key = LIST_KEY_PREFIX + cacheKey;
        try {
            String json = objectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(key, json, LIST_TTL);
        } catch (JsonProcessingException e) {
            log.warn("product list cache serialization failed: key={}", cacheKey, e);
        }
    }
}
