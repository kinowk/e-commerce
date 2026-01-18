package com.loopers.domain.point;

public class PointResult {
    public record GetPoint(String loginId, Long balance) {
        public static GetPoint from(String loginId, Point point) {
            return new GetPoint(
                    loginId,
                    point.getBalance()
            );
        }
    }

    public record Charge(String loginId, Long amount, Long balance) {
    }
}
