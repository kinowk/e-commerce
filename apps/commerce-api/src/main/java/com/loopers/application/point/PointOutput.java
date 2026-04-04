package com.loopers.application.point;

import com.loopers.domain.point.PointResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointOutput {
    public record GetPoint(String loginId, Long balance) {
        public static GetPoint from(PointResult.GetPoint result) {
            return new GetPoint(
                    result.loginId(),
                    result.balance()
            );
        }
    }

    public record Charge(String loginId, Long amount, Long balance) {
        public static Charge from(PointResult.Charge result) {
            return new Charge(
                    result.loginId(),
                    result.amount(),
                    result.balance()
            );
        }
    }
}
