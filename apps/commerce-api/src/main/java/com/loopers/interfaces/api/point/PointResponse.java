package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointOutput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointResponse {

    public record Charge(Long id, Long userId, Long balance) {
        public static Charge from(PointOutput.Charge output) {
            return new Charge(
                    output.id(),
                    output.userId(),
                    output.balance()
            );
        }
    }

    public record GetPoint(Long userId, Long balance) {
        public static GetPoint from(PointOutput.GetPoint output) {
            return new GetPoint(
                    output.userId(),
                    output.balance()
            );
        }
    }
}
