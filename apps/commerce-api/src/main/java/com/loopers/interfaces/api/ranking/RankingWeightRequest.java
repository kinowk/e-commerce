package com.loopers.interfaces.api.ranking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingWeightRequest {
    public record UpdateWeight(double weight) {}
}
