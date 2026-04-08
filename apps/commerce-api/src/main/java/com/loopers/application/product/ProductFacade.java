package com.loopers.application.product;

import com.loopers.domain.product.ProductService;
import com.loopers.domain.ranking.RankingResult;
import com.loopers.domain.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final RankingService rankingService;

    public ProductOutput.Detail getProduct(Long productId) {
        ProductOutput.Detail detail = ProductOutput.Detail.from(productService.getProduct(productId));
        RankingResult.ProductRankInfo rankInfo = rankingService.getProductRankInfo(LocalDate.now(), productId);
        if (rankInfo != null) {
            return detail.withRank(rankInfo.rank(), rankInfo.score());
        }
        return detail;
    }

    public ProductOutput.List getProducts(ProductInput.Query input) {
        return ProductOutput.List.from(productService.getProducts(input.toCommand()));
    }
}
