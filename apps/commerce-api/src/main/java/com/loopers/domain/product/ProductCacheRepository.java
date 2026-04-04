package com.loopers.domain.product;

import java.util.Optional;

public interface ProductCacheRepository {

    Optional<ProductResult.Detail> getDetail(Long productId);

    void putDetail(Long productId, ProductResult.Detail detail);

    void evictDetail(Long productId);

    Optional<ProductResult.List> getList(String cacheKey);

    void putList(String cacheKey, ProductResult.List list);
}
