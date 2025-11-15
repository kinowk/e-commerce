package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long balance;

    @Builder
    public Point(Long userId, Long balance) {
        validateBalance(balance);

        this.userId = userId;
        this.balance = balance;
    }

    private void validateBalance(Long balance) {
        if (balance == null || balance < 0)
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트 잔액은 0 이상이어야 합니다.");
    }

    public long charge(Long amount) {
        if (amount <= 0)
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 금액은 0보다 커야 합니다.");

        if (this.balance > Long.MAX_VALUE - amount)
            throw new CoreException(ErrorType.BAD_REQUEST, "잔액 한도를 초과할 수 없습니다.");

        this.balance += amount;

        return this.balance;
    }
}
