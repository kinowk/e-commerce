package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingWeightFacade;
import com.loopers.application.ranking.RankingWeightInput;
import com.loopers.application.ranking.RankingWeightOutput;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/ranking/weights")
public class RankingWeightV1Controller {

    private final RankingWeightFacade weightFacade;

    @GetMapping
    public ApiResponse<RankingWeightResponse.GetWeights> getWeights() {
        RankingWeightOutput.GetWeights output = weightFacade.getWeights();
        RankingWeightResponse.GetWeights response = RankingWeightResponse.GetWeights.from(output);
        return ApiResponse.success(response);
    }

    @PutMapping("/{type}")
    public ApiResponse<Void> updateWeight(
            @PathVariable String type,
            @RequestBody RankingWeightRequest.UpdateWeight request
    ) {
        RankingWeightInput.UpdateWeight input = new RankingWeightInput.UpdateWeight(
                type,
                request.weight()
        );
        weightFacade.updateWeight(input);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{type}")
    public ApiResponse<Void> resetWeight(@PathVariable String type) {
        RankingWeightInput.ResetWeight input = new RankingWeightInput.ResetWeight(type);
        weightFacade.resetWeight(input);
        return ApiResponse.success(null);
    }
}
