package com.loopers.application.ranking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingWeightInput {
    public record UpdateWeight(String type, double weight) {}
    public record ResetWeight(String type) {}
}
