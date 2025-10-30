package com.loopers.domain.point;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointCommand {

    public record Charge(Long userId, Long amount) {
    }

    public record Use(Long userId, Long amount) {
    }
}
