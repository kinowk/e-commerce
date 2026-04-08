package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.List;

public interface RankingRepository {
    List<RankingResult.Entry> getRankingPage(LocalDate date, int offset, int size);
    Long getRank(LocalDate date, Long productId);
    Double getScore(LocalDate date, Long productId);
}
