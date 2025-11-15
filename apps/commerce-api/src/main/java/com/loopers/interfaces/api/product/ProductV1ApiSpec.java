package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product V1 API", description = "상품 관련 API")
public interface ProductV1ApiSpec {

    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록을 조회합니다."
    )
    ApiResponse<ProductResponse.GetProductSummary> getProductList(
        @RequestBody(description = "상품 조회 조건") ProductRequest.GetProductList request
    );

    @Operation(
            summary = "상품 상세 조회",
            description = "상품을 상세 조회합니다."
    )
    ApiResponse<ProductResponse.GetProductDetail> getProduct(
            @Schema(name = "상품 ID", description = "조회할 상품 Id") Long productId
    );
}
