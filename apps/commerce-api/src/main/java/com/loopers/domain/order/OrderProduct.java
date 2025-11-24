package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_products")
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "ref_product_option_id", nullable = false)
    private Long productOptionId;

    private Integer quantity;

    private Integer price;

    public OrderProduct(Long orderId, Long productOptionId, Integer quantity, Integer price) {
        if (quantity == null || quantity <= 0)
            throw new CoreException(ErrorType.BAD_REQUEST, "수량은 1개 이상이어야 됩니다.");

        if (price == null || price < 0)
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 금액은 0원 이상이어야 합니다.");

        if (orderId == null)
            throw new CoreException(ErrorType.BAD_REQUEST, "주문이 올바르지 않습니다.");

        if (productOptionId == null)
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 옵션이 올바르지 않습니다.");

        this.orderId = orderId;
        this.productOptionId = productOptionId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getTotalPrice() {
        return this.quantity * this.price;
    }
}
