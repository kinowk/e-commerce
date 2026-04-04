package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointOutput;

public class PointResponse {
    public record GetPoint(Long userId, Long balance) {
        public static GetPoint from(PointOutput.GetPoint output) {
            return new GetPoint(
                    output.userId(),
                    output.balance()
            );
        }
    }

    public record Charge(Long userId, Long amount, Long balance) {
        public static Charge from(PointOutput.Charge output) {
            return new Charge(
                    output.userId(),
                    output.amount(),
                    output.balance()
            );
        }
    }
}
