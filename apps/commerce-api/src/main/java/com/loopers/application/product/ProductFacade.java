package com.loopers.application.product;

import com.loopers.domain.brand.BrandResult;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductResult;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final BrandService brandService;
    private final LikeService likeService;

    public ProductOutput.GetProductSummary getProductSummary(ProductInput.GetProductList input) {
        ProductCommand.GetProductSummary command = input.toCommand();
        ProductResult.GetProductSummary result = productService.getProductSummary(command);
        return ProductOutput.GetProductSummary.from(result);
    }

    public ProductOutput.GetProductDetail getProductDetail(Long productId) {
        ProductResult.GetProductDetail productDetail = productService.getProductDetail(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품이 존재하지 않습니다."));

        BrandResult.GetBrand getBrand = brandService.findBrand(productDetail.brandId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        Long likeCount = likeService.getLikeCount(productId);

        return ProductOutput.GetProductDetail.from(productDetail, likeCount, getBrand);
    }
}
