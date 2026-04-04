package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Order V1 API", description = "주문 관련 API")
public interface OrderV1ApiSpec {

    @Operation(
            summary = "주문 생성",
            description = "상품을 주문합니다."
    )
    ApiResponse<OrderResponse.Create> createOrder(String userLoginId, OrderRequest.Create request);

    @Operation(
            summary = "주문 목록 조회",
            description = "사용자의 주문 목록을 조회합니다."
    )
    ApiResponse<List<OrderResponse.Summary>> getOrders(String userLoginId);

    @Operation(
            summary = "주문 상세 조회",
            description = "주문 ID로 주문 상세 정보를 조회합니다."
    )
    ApiResponse<OrderResponse.Detail> getOrder(String userLoginId, Long orderId);
}
