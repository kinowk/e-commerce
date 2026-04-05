package com.loopers.domain.point;

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
@Table(name = "points")
public class Point extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;

    @Column(name = "ref_user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "balance", nullable = false)
    private Long balance;

    @Version
    private Long version;

    private static final long MAX_BALANCE = Long.MAX_VALUE;

    public Point(Long userId, Long balance) {
        validateBalance(balance);

        this.userId = userId;
        this.balance = balance;
    }

    private void validateBalance(Long initialBalance) {
        if (initialBalance == null || initialBalance < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잔액은 0 이상이어야 합니다.");
        }
    }

    public Long charge(Long amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 금액은 0보다 커야 합니다.");
        }

        if (MAX_BALANCE - amount < this.balance) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 후 잔액이 최대 한도를 초과합니다.");
        }

        this.balance += amount;
        return this.balance;
    }

    public Long deduct(Long amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "차감 금액은 0보다 커야 합니다.");
        }

        if (!hasSufficientBalance(amount)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잔액이 부족합니다.");
        }

        this.balance -= amount;

        return this.balance;
    }

    public boolean hasSufficientBalance(Long amount) {
        return amount != null && amount > 0 && balance >= amount;
    }

}
