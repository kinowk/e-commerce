package com.loopers.domain.ranking;

public record RankingResult(
        Long rank,
        Long productId,
        Double score
) {
    public static RankingResult of(Long rank, Long productId, Double score) {
        return new RankingResult(rank, productId, score);
    }
}
