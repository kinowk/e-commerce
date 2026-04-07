package com.loopers.application;

import com.loopers.domain.metrics.ProductMetrics;
import com.loopers.infrastructure.metrics.ProductMetricsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ProductMetricsService {

    private final ProductMetricsJpaRepository productMetricsRepository;

    @Transactional
    public void incrementLikeCount(Long productId) {
        updateMetrics(productId, ProductMetrics::incrementLikeCount);
    }

    @Transactional
    public void decrementLikeCount(Long productId) {
        updateMetrics(productId, ProductMetrics::decrementLikeCount);
    }

    @Transactional
    public void addSalesCount(Long productId, long quantity) {
        updateMetrics(productId, m -> m.addSalesCount(quantity));
    }

    @Transactional
    public void incrementViewCount(Long productId) {
        updateMetrics(productId, ProductMetrics::incrementViewCount);
    }

    private void updateMetrics(Long productId, Consumer<ProductMetrics> mutation) {
        ProductMetrics metrics = productMetricsRepository.findById(productId)
                .orElseGet(() -> productMetricsRepository.save(new ProductMetrics(productId)));
        mutation.accept(metrics);
        productMetricsRepository.save(metrics);
    }
}
