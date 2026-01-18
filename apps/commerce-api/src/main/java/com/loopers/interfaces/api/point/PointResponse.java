package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointOutput;

public class PointResponse {
    public record GetPoint(String loginId, Long balance) {
        public static GetPoint from(PointOutput.GetPoint output) {
            return new GetPoint(
                    output.loginId(),
                    output.balance()
            );
        }
    }

    public record Charge(String loginId, Long amount, Long balance) {
        public static Charge from(PointOutput.Charge output) {
            return new Charge(
                    output.loginId(),
                    output.amount(),
                    output.balance()
            );
        }
    }
}
