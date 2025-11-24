package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_option_id")
    private Long id;

    @Column(name = "color")
    private String color;

    @Column(name = "size")
    private String size;

    @Column(name = "extra_price", nullable = false)
    private Integer extraPrice;

    @OneToOne(mappedBy = "productOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stock stock;

    @Builder
    private ProductOption(String color, String size, Integer extraPrice, Integer quantity) {
        if (extraPrice == null || extraPrice < 0)
            throw new CoreException(ErrorType.BAD_REQUEST, "추가 가격은 0원 이상이어야 합니다.");

        if (quantity == null)
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 옵션의 재고가 필요합니다.");

        this.color = color;
        this.size = size;
        this.extraPrice = extraPrice;
        this.stock = Stock.builder()
                .productOption(this)
                .quantity(quantity)
                .build();

    }

    public int getStockQuantity() {
        return this.stock.getQuantity();
    }

    public void decreaseStock(int quantity) {
        this.stock.deduct(quantity);
    }
}
