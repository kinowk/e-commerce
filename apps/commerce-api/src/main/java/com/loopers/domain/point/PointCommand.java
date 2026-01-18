package com.loopers.domain.point;

public class PointCommand {
    public record Charge(String loginId, Long amount) {
    }
}
