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
    private Long id;

    @Column(name = "ref_user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "balance", nullable = false)
    private Long balance;

    private static Long MAX_BALANCE = Long.MAX_VALUE;

    public Point(Long userId, Long balance) {
        validateBalance(balance);

        this.userId = userId;
        this.balance = balance;
    }

    private void validateBalance(Long balance) {
        if (balance == null || balance < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }
    }

    public Long charge(Long amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }

        if (MAX_BALANCE - amount < this.balance) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }

        this.balance += amount;
        return this.balance;
    }

    public Long deduct(Long amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }

        if (!hasSufficientBalance(amount)) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }

        this.balance -= amount;

        return this.balance;
    }

    public boolean hasSufficientBalance(Long amount) {
        return amount != null && amount > 0 && balance >= amount;
    }

}
