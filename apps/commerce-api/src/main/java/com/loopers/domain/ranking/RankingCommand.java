package com.loopers.domain.ranking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingCommand {

    public record GetRankings(String date, int page, int size) {}
}
