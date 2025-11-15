package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductInput;
import com.loopers.application.product.ProductOutput;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec {

    private final ProductFacade productFacade;

    @GetMapping
    @Override
    public ApiResponse<ProductResponse.GetProductSummary> getProductList(ProductRequest.GetProductList request) {
        ProductInput.GetProductList input = request.toInput();
        ProductOutput.GetProductSummary output = productFacade.getProductSummary(input);
        ProductResponse.GetProductSummary response = ProductResponse.GetProductSummary.from(output);
        return ApiResponse.success(response);
    }

    @GetMapping("/{productId}")
    @Override
    public ApiResponse<ProductResponse.GetProductDetail> getProduct(@PathVariable Long productId) {
        ProductOutput.GetProductDetail output = productFacade.getProductDetail(productId);
        ProductResponse.GetProductDetail response = ProductResponse.GetProductDetail.from(output);
        return ApiResponse.success(response);
    }
}
