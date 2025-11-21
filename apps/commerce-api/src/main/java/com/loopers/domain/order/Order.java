package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "ref_user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ref_order_id")
    private List<OrderProduct> products = new ArrayList<>();

    @Builder
    public Order(Long userId, Long totalPrice) {
        if (totalPrice == null || totalPrice < 0)
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 금액은 0원 이상이어야 합니다.");

        if (userId == null)
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자가 올바르지 않습니다.");

        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.CREATED;
    }

    public void addProduct(List<OrderProduct> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty())
            return;

        if (this.products == null || this.products.isEmpty())
            this.products = new ArrayList<>(orderProducts);
        else
            this.products.addAll(orderProducts);

        calculateTotalPrice();
    }

    public void removeProduct(List<OrderProduct> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty() || this.products.isEmpty())
            return;

        this.products.removeAll(orderProducts);
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        this.totalPrice = products.stream()
                .mapToLong(product -> {
                    return (long) product.getPrice() * product.getQuantity();
                })
                .sum();
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }

}
