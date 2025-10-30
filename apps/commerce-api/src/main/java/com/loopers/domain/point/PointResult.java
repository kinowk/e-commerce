package com.loopers.domain.point;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointResult {

    @Getter
    @Builder
    public static class GetPoint {
        private final Long id;
        private final Long userId;
        private final Long balance;

        public static GetPoint from(Point point) {
            return GetPoint.builder()
                    .id(point.getId())
                    .userId(point.getUserId())
                    .balance(point.getBalance())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Charge {
        private final Long id;
        private final Long userId;
        private final Long balance;

        public static Charge from(Point point) {
            return Charge.builder()
                    .id(point.getId())
                    .userId(point.getUserId())
                    .balance(point.getBalance())
                    .build();
        }
    }
}
