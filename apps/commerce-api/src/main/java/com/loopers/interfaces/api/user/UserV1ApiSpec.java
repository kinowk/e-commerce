package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User V1 API", description = "회원 관련 API")
public interface UserV1ApiSpec {

    @Operation(
            summary = "사용자 등록",
            description = "신규 사용자를 등록합니다."
    )
    ApiResponse<UserResponse.Join> join(
            @RequestBody(description = "사용자 정보") UserRequest.Join request
    );

    @Operation(
            summary = "사용자 조회",
            description = "로그인 ID로 사용자를 조회합니다."
    )
    ApiResponse<UserResponse.GetUser> getUser(
            @Schema(name = "로그인 ID", description = "조회할 사용자 ID") String loginId
    );

}
