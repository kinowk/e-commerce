package com.loopers.intrastructure.metric;

import com.loopers.domain.metric.ProductMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public interface ProductMetricDailyJpaRepository extends JpaRepository<ProductMetric, Long> {

    @Modifying
    @Query("""
            insert into ProductMetricDaily (date, productId, likeCount, saleQuantity, viewCount, createdAt, updatedAt)
            values (:date, :productId, :likeCount, :saleQuantity, :viewCount, :createdAt, :updatedAt)
            on conflict (date, productId)
            do update set likeCount =  likeCount + :likeCount, saleQuantity = saleQuantity + :saleQuantity, viewCount = viewCount + :viewCount, updatedAt = :updatedAt
            """)
    int merge(
            @Param("date") LocalDate date,
            @Param("productId") Long productId,
            @Param("likeCount") Long likeCount,
            @Param("saleQuantity") Long saleQuantity,
            @Param("viewCount") Long viewCount,
            @Param("createdAt") ZonedDateTime createdAt,
            @Param("updatedAt") ZonedDateTime updatedAt
    );
}
