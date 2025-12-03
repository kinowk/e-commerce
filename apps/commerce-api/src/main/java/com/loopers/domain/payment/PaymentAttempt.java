package com.loopers.domain.payment;

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
@Table(name = "payment_attemps", indexes = {
        @Index(name = "", columnList = "ref_payment_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_attempt_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "ref_payment_id", nullable = false, updatable = false)
    private Long paymentId;

    @Column(name = "order_uid", nullable = false, unique = true)
    private String orderUid;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "pg_key")
    private String pgKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "attempt_status", nullable = false)
    private AttemptStatus attemptStatus;

    @Column(name = "description")
    private String description;

    @Builder
    private PaymentAttempt(Long paymentId, String orderUid, Long amount, PaymentMethod paymentMethod, String description) {
        if (amount == null || amount < 0)
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 0원 이상이어야 됩니다.");

        if (paymentId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 결제 요청입니다.");
        }

        if (paymentMethod == null)
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 결제 수단입니다.");

        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.orderUid = orderUid;
        this.attemptStatus = AttemptStatus.PENDING;
        this.description = description;
    }

    public void success(String pgKey) {
        this.pgKey = pgKey;
        this.attemptStatus = AttemptStatus.SUCCESS;
    }

    public void fail(String description) {
        this.description = description;
        this.attemptStatus = AttemptStatus.FAILED;
    }
}
