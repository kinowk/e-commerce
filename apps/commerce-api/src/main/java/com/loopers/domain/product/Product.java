package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "base_price", nullable = false)
    private Integer basePrice;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "ref_brand_id", nullable = false)
    private Long brandId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    @Builder
    private Product(String name, Integer basePrice, Long brandId) {
        if (!StringUtils.hasText(name))
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 이름이 올바르지 않습니다.");

        if (basePrice == null || basePrice < 0)
            throw new CoreException(ErrorType.BAD_REQUEST, "기본 가격은 0원 이상이어야 합니다.");

        this.name = name;
        this.basePrice = basePrice;
        this.brandId = brandId;
        this.likeCount = 0L;
    }

    public void like() {
        if (this.likeCount < Long.MAX_VALUE)
            this.likeCount++;
    }

    public void dislike() {
        if (this.likeCount > 0)
            this.likeCount--;
    }
}
