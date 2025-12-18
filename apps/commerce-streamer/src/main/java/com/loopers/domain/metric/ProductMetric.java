package com.loopers.domain.metric;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Entity
@Table(name = "product_metrics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metric_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "metric_date", nullable = false, updatable = false)
    private LocalDate date;

    @Column(name = "ref_product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "sale_quantity", nullable = false)
    private Long saleQuantity;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Builder
    private ProductMetric(LocalDate date, Long productId, Long likeCount, Long saleQuantity, Long viewCount) {
        if (date == null)
            throw new IllegalArgumentException();

        if (productId == null)
            throw new IllegalArgumentException();

        if (likeCount != null && likeCount < 0)
            throw new IllegalArgumentException();

        if (saleQuantity != null && saleQuantity < 0)
            throw new IllegalArgumentException();

        if (viewCount != null && viewCount < 0)
            throw new IllegalArgumentException();

        this.date = date;
        this.productId = productId;
        this.likeCount = Objects.requireNonNullElse(likeCount, 0L);
        this.saleQuantity = Objects.requireNonNullElse(saleQuantity, 0L);
        this.viewCount = Objects.requireNonNullElse(viewCount, 0L);
    }
}
