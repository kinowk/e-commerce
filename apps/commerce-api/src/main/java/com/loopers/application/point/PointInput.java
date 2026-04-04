package com.loopers.application.point;

import com.loopers.domain.point.PointCommand;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointInput {

    public record Charge(String loginId, Long amount) {
        public PointCommand.Charge toCommand() {
            return new PointCommand.Charge(loginId, amount);
        }
    }

}
