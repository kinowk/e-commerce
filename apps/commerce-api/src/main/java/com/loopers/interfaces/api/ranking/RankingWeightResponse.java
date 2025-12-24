package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingWeightOutput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingWeightResponse {
    public record GetWeights(double view, double like, double order) {
        public static GetWeights from(RankingWeightOutput.GetWeights output) {
            return new GetWeights(output.view(), output.like(), output.order());
        }
    }
}
