package com.loopers.domain.order;

import com.loopers.domain.BaseTimeEntity;
import com.loopers.domain.order.attribute.OrderStatus;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login_id", nullable = false)
    private String userLoginId;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    public Order(String userLoginId, Long totalAmount) {
        if (!StringUtils.hasText(userLoginId)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 유효하지 않습니다.");
        }
        if (totalAmount == null || totalAmount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 금액이 유효하지 않습니다.");
        }
        this.userLoginId = userLoginId;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PAID;
    }
}
