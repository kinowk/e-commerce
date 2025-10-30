package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Point V1 API", description = "포인트 관련 API")
public interface PointV1ApiSpec {

    @Operation(
            summary = "포인트 조회",
            description = "사용자 ID로 포인트를 조회합니다."
    )
    ApiResponse<PointResponse.GetPoint> getPoint(
            @Parameter(name = "X-USER-ID", description = "헤더 ID", in = ParameterIn.HEADER, required = true) Long header,
            @PathVariable Long userId
    );

    @Operation(
            summary = "포인트 충전",
            description = "회원의 포인트를 충전합니다."
    )
    ApiResponse<PointResponse.Charge> charge(@RequestBody PointRequest.Charge request);

}
