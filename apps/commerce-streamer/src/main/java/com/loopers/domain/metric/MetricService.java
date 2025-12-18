package com.loopers.domain.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final MetricRepository metricRepository;

    @Transactional
    public void aggregateProduct(MetricCommand.AggregateProduct command) {
        ProductMetric metric = ProductMetric.builder()
                .date(command.date())
                .productId(command.productId())
                .likeCount(command.likeCount())
                .saleQuantity(command.saleQuantity())
                .viewCount(command.viewCount())
                .build();

        metricRepository.merge(metric);
    }
}
