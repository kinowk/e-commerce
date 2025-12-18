package com.loopers.application.metric;

import com.loopers.domain.metric.MetricCommand;
import com.loopers.domain.metric.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MetricFacade {

    private final MetricService metricService;

    @Transactional
    public void aggregateProduct(MetricInput.AggregateProduct input) {
        input.items().stream()
                .map(item -> new MetricCommand.AggregateProduct(item.localDate(), item.productId(), item.likeCount(), item.saleQuantity(), item.viewCount()))
                .forEach(metricService::aggregateProduct);
    }
}
