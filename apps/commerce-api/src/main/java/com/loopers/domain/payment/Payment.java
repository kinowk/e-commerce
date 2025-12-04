package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Getter
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_order_user", columnList = "ref_order_id, ref_user_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "ref_order_id", nullable = false, updatable = false)
    private Long orderId;

    @Column(name = "ref_user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private PaymentCardType paymentCardType;

    @Column(name = "card_no")
    private String cardNo;

    private static final Pattern CARD_NO_PATTERN = Pattern.compile("^\\d{4}(-\\d{4}){3}$");

    @Builder
    private Payment(Long orderId, Long userId, Long amount, PaymentStatus status, PaymentMethod method, String cardNo, PaymentCardType paymentCardType) {
        if (amount == null || amount < 0)
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 0원 이상이어야 합니다.");

        if (status == null)
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 상태가 올바르지 않습니다.");

        switch (method) {
            case CARD -> {
                if (paymentCardType == null)
                    throw new CoreException(ErrorType.BAD_REQUEST, "카드종류가 올바르지 않습니다.");
                if (cardNo == null || !CARD_NO_PATTERN.matcher(cardNo).matches())
                    throw new CoreException(ErrorType.BAD_REQUEST, "카드번호가 올바르지 않습니다.");
            }
            case POINT -> {

            }
            default -> throw new CoreException(ErrorType.BAD_REQUEST, "결제 수단이 올바르지 않습니다.");
        }




        if (userId == null)
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자가 올바르지 않습니다.");

        if (orderId == null)
            throw new CoreException(ErrorType.BAD_REQUEST, "주문이 올바르지 않습니다.");

        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.method = method;
        this.cardNo = cardNo;
        this.paymentCardType = paymentCardType;
    }
}
