package com.loopers.interfaces.api.ranking;

import com.loopers.domain.ranking.RankingResult;
import com.loopers.domain.ranking.RankingService;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
public class RankingV1Controller {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final RankingService rankingService;

    @GetMapping
    public ApiResponse<RankingResponse.Page> getRankings(
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "1") int page
    ) {
        if (page < 1 || size < 1 || size > 100) {
            throw new CoreException(ErrorType.BAD_REQUEST, "page는 1 이상, size는 1~100 사이여야 합니다.");
        }
        LocalDate rankingDate = date != null ? LocalDate.parse(date, DATE_FORMAT) : LocalDate.now();
        RankingResult.Page result = rankingService.getRankingPage(rankingDate, page, size);
        return ApiResponse.success(RankingResponse.Page.from(result));
    }
}
