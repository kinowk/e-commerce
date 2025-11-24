package com.loopers.application.point;

import com.loopers.domain.point.PointResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointOutput {

    public record Charge(Long id, Long userId, Long balance) {
        public static Charge from(PointResult.Charge result) {
            return new Charge(
                    result.getId(),
                    result.getUserId(),
                    result.getBalance()
            );
        }
    }

    public record GetPoint(Long userId, Long balance) {
        public static GetPoint from(PointResult.GetPoint result) {
            return new GetPoint(
                    result.getUserId(),
                    result.getBalance()
            );
        }
    }
}
