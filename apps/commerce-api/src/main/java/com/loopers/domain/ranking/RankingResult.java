package com.loopers.domain.ranking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingResult {

    public record Entry(Long rank, Long productId, Double score) {
    }

    public record Page(String date, int page, int size, List<ProductRanking> rankings) {
    }

    public record ProductRanking(Long rank, Long productId, String productName, String brandName,
                                 Long price, Long likeCount, Double score) {
    }

    public record ProductRankInfo(Long rank, Double score) {
    }
}
