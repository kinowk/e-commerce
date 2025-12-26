package com.loopers.application.ranking;

import com.loopers.domain.ranking.RankingResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingOutput {

    public record GetRankings(List<RankingItem> items, int page, int size, long total) {
        public static GetRankings from(List<RankingResult> results, int page, int size, long total) {
            List<RankingItem> items = results.stream()
                    .map(result -> new RankingItem(
                            result.rank(),
                            result.productId(),
                            result.score()
                    ))
                    .toList();
            return new GetRankings(items, page, size, total);
        }
    }

    public record RankingItem(Long rank, Long productId, Double score) {}
}
