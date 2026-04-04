package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiHeader;
import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Point V1 API", description = "포인트 관련 API")
public interface PointV1ApiSpec {

    @Operation(
            summary = "보유 포인트 조회",
            description = "보유한 포인트를 조회합니다."
    )
    ApiResponse<PointResponse.GetPoint> getPoint(
            @Schema(name = ApiHeader.X_USER_ID, description = "사용자 ID") Long userId
    );

    @Operation(
            summary = "포인트 충전",
            description = "포인트를 충전합니다."
    )
    ApiResponse<PointResponse.Charge> charge(
            @Schema(name = ApiHeader.X_USER_ID, description = "사용자 ID") Long userId,
            @RequestBody(description = "충전 정보") PointRequest.Charge request
    );
}
