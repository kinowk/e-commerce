package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order V1 API", description = "주문 관련 API")
public interface OrderV1ApiSpec {

    @Operation(
            summary = "사용자 주문 목록 조회",
            description = "사용자의 주문 목록을 조회합니다."
    )
    ApiResponse<OrderResponse.GetOrderSummary> getOrderSummary(
            @Schema(name = "X-USER-ID", description = "조회할 사용자 ID") Long userId
    );

    @Operation(
            summary = "주문 상세 조회",
            description = "주문 ID로 주문를 조회합니다."
    )
    ApiResponse<OrderResponse.GetOrderDetail> getOrderDetail(
            @Schema(name ="X-USER-ID", description = "조회할 사용자 ID") Long userId,
            @Schema(name = "주문 ID", description = "조회할 주문 ID") Long orderId
    );

    @Operation(
            summary = "주문 요청",
            description = "주문을 요청합니다."
    )
    ApiResponse<OrderResponse.Create> create(
        @Schema(name = "X-USER-ID", description = "조회할 사용자 ID") Long userId,
        @RequestBody(description = "주문 정보") OrderRequest.Create request
    );
}
