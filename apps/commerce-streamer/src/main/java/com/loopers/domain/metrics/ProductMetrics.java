package com.loopers.domain.metrics;

import com.loopers.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_metrics")
public class ProductMetrics extends BaseTimeEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "sales_count", nullable = false)
    private Long salesCount;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    public ProductMetrics(Long productId) {
        this.productId = productId;
        this.likeCount = 0L;
        this.salesCount = 0L;
        this.viewCount = 0L;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void addSalesCount(long quantity) {
        this.salesCount += quantity;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}
