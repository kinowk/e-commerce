package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.domain.product.attribute.ProductSortType;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec {

    private final ProductFacade productFacade;

    @GetMapping("/{productId}")
    @Override
    public ApiResponse<ProductResponse.Detail> getProduct(@PathVariable Long productId) {
        return ApiResponse.success(ProductResponse.Detail.from(productFacade.getProduct(productId)));
    }

    @GetMapping
    @Override
    public ApiResponse<ProductResponse.List> getProducts(
            @ModelAttribute ProductRequest.Query query
    ) {
        return ApiResponse.success(ProductResponse.List.from(productFacade.getProducts(query.toInput())));
    }
}
