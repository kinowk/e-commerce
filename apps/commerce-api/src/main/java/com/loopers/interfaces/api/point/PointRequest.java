package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointRequest {

    public record Charge(Long userId, Long amount) {
        public PointInput.Charge toInput() {
            return new PointInput.Charge(userId, amount);
        }
    }

    public record Use(Long userId, Long amount) {
        public PointInput.Use toInput() {
            return new PointInput.Use(userId, amount);
        }
    }
}
