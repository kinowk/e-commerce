package com.loopers.domain.ranking;

public record RankingWeightCommand() {
    public record UpdateWeight(String type, double weight) {}
    public record ResetWeight(String type) {}
}
