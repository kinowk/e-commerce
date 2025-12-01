package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_BRAND_KEY = "brand:";

    public BrandResult.GetBrand getBrand(Long brandId) {
        String key = REDIS_BRAND_KEY + brandId;

        Object cached = null;
        try {
            cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof BrandResult.GetBrand result)
                return result;
        } catch (RedisConnectionFailureException | SerializationException e) {
            log.warn("Redis 조회 실패: {}", key, e);
        }

        BrandResult.GetBrand result = brandRepository.findById(brandId)
                .map(BrandResult.GetBrand::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다."));

        try {
            redisTemplate.opsForValue().set(key, result, Duration.ofMinutes(15));
        } catch (RedisConnectionFailureException | SerializationException e) {
            log.warn("Redis 저장 실패: {}", key, e);
        }

        return result;
    }

}
