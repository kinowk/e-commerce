package com.loopers.application.point;

import com.loopers.domain.point.PointCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointInput {

    public record Charge(Long userId, Long amount) {
        public PointCommand.Charge toCommand() {
            return new PointCommand.Charge(
                    userId,
                    amount
            );
        }
    }

    public record Use(Long userId, Long amount) {
        public PointCommand.Use toCommand() {
            return new PointCommand.Use(
                    userId,
                    amount
            );
        }
    }
}
