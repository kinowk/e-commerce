package com.loopers.application.ranking;

import com.loopers.domain.ranking.RankingWeightCommand;
import com.loopers.domain.ranking.RankingWeightResult;
import com.loopers.domain.ranking.RankingWeightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingWeightFacade {

    private final RankingWeightService weightService;

    public void updateWeight(RankingWeightInput.UpdateWeight input) {
        RankingWeightCommand.UpdateWeight command = new RankingWeightCommand.UpdateWeight(
                input.type(),
                input.weight()
        );
        weightService.updateWeight(command);
    }

    public void resetWeight(RankingWeightInput.ResetWeight input) {
        RankingWeightCommand.ResetWeight command = new RankingWeightCommand.ResetWeight(
                input.type()
        );
        weightService.resetWeight(command);
    }

    public RankingWeightOutput.GetWeights getWeights() {
        RankingWeightResult.GetWeights result = weightService.getWeights();
        return new RankingWeightOutput.GetWeights(
                result.view(),
                result.like(),
                result.order()
        );
    }
}
