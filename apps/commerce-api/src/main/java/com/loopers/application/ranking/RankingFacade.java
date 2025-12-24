package com.loopers.application.ranking;

import com.loopers.domain.ranking.RankingCommand;
import com.loopers.domain.ranking.RankingResult;
import com.loopers.domain.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RankingFacade {

    private final RankingService rankingService;

    public RankingOutput.GetRankings getRankings(RankingInput.GetRankings input) {
        RankingCommand.GetRankings command = new RankingCommand.GetRankings(
                input.date(),
                input.page(),
                input.size()
        );
        List<RankingResult> results = rankingService.getRankings(
                command.date(),
                command.page(),
                command.size()
        );
        return RankingOutput.GetRankings.from(results, input.page(), input.size());
    }

    public Long getProductRank(String date, Long productId) {
        return rankingService.getProductRank(date, productId);
    }
}
