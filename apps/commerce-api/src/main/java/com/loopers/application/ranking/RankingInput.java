package com.loopers.application.ranking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingInput {

    public record GetRankings(String date, int page, int size) {
        public static GetRankings of(String date, int page, int size) {
            return new GetRankings(date, page, size);
        }
    }
}
