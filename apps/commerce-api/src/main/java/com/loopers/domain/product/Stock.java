package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Entity
@Table(name = "stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "ref_product_option_id", nullable = false, updatable = false, unique = true)
    private Long productOptionId;

    @Embedded
    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Builder
    public Stock(Integer quantity, ZonedDateTime updatedAt) {
        if (quantity == null || quantity < 0)
            throw new CoreException(ErrorType.BAD_REQUEST);

        this.quantity = quantity;
        this.updatedAt = ZonedDateTime.now();
    }

    public void deduct(int amount) {
        if (this.quantity < amount)
            throw new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다.");

        this.quantity = this.quantity - amount;
    }
}
