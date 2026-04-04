package com.loopers.domain.point;

public class PointResult {
    public record GetPoint(Long userId, Long balance) {
        public static GetPoint from(Long userId, Point point) {
            return new GetPoint(
                    userId,
                    point.getBalance()
            );
        }
    }

    public record Charge(Long userId, Long amount, Long balance) {
    }
}
