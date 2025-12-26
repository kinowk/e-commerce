package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingOutput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingResponse {

    public record GetRankings(List<RankingItem> items, int page, int size, long total) {
        public static GetRankings from(RankingOutput.GetRankings output) {
            List<RankingItem> items = output.items().stream()
                    .map(item -> new RankingItem(
                            item.rank(),
                            item.productId(),
                            item.score()
                    ))
                    .toList();
            return new GetRankings(items, output.page(), output.size(), output.total());
        }
    }

    public record RankingItem(Long rank, Long productId, Double score) {}
}
