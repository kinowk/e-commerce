package com.loopers.domain.ranking;

public record RankingWeightResult() {
    public record GetWeights(double view, double like, double order) {}
}
