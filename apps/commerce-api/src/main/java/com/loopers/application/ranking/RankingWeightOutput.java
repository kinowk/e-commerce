package com.loopers.application.ranking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingWeightOutput {
    public record GetWeights(double view, double like, double order) {}
}
