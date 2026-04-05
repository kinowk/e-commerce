package com.loopers.domain.payment;

import com.loopers.domain.BaseTimeEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payments_ref_order_id", columnList = "ref_order_id"),
                @Index(name = "idx_payments_transaction_id", columnList = "transaction_id")
        }
)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "ref_order_id", nullable = false)
    private Long orderId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "card_type", nullable = false)
    private String cardType;

    @Column(name = "card_no", nullable = false)
    private String cardNo;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    public Payment(Long orderId, String cardType, String cardNo, Long amount) {
        if (orderId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "orderId는 필수입니다.");
        }
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 0보다 커야 합니다.");
        }
        this.orderId = orderId;
        this.cardType = cardType;
        this.cardNo = cardNo;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public void registerTransaction(String transactionId) {
        this.transactionId = transactionId;
    }

    public void complete() {
        this.status = PaymentStatus.COMPLETED;
    }

    public void fail(PaymentStatus failStatus) {
        if (failStatus == PaymentStatus.PENDING || failStatus == PaymentStatus.COMPLETED) {
            throw new CoreException(ErrorType.BAD_REQUEST, "실패 상태만 설정할 수 있습니다.");
        }
        this.status = failStatus;
    }

    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }
}
