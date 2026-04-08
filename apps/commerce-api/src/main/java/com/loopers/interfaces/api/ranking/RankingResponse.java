package com.loopers.interfaces.api.ranking;

import com.loopers.domain.ranking.RankingResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingResponse {

    public record Page(String date, int page, int size, List<ProductRanking> rankings) {
        public static Page from(RankingResult.Page result) {
            return new Page(
                    result.date(),
                    result.page(),
                    result.size(),
                    result.rankings().stream().map(ProductRanking::from).toList()
            );
        }
    }

    public record ProductRanking(Long rank, Long productId, String productName,
                                 String brandName, Long price, Long likeCount, Double score) {
        public static ProductRanking from(RankingResult.ProductRanking result) {
            return new ProductRanking(
                    result.rank(), result.productId(), result.productName(),
                    result.brandName(), result.price(), result.likeCount(), result.score()
            );
        }
    }
}
