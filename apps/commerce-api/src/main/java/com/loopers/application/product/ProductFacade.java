package com.loopers.application.product;

import com.loopers.application.ranking.RankingFacade;
import com.loopers.domain.brand.BrandResult;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductResult;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final BrandService brandService;
    private final LikeService likeService;
    private final RankingFacade rankingFacade;

    public ProductOutput.GetProductSummary getProductSummary(ProductInput.GetProductList input) {
        ProductCommand.GetProductSummary command = input.toCommand();
        ProductResult.GetProductSummary result = productService.getProductSummary(command);
        return ProductOutput.GetProductSummary.from(result);
    }

    public ProductOutput.GetProductDetail getProductDetail(Long productId) {
        ProductResult.GetProductDetail productDetail = productService.getProductDetail(productId);

        BrandResult.GetBrand brand = brandService.getBrand(productDetail.brandId());

        Long likeCount = likeService.getLikeCount(productId);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long rank = rankingFacade.getProductRank(today, productId);

        return ProductOutput.GetProductDetail.from(productDetail, likeCount, brand, rank);
    }
}
