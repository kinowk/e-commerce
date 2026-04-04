package com.loopers.interfaces.api.point;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointRequest {

    public record Charge(@NotNull Long amount) {
    }

}
