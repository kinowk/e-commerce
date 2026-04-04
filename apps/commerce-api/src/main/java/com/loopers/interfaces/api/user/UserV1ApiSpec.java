package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiHeader;
import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User V1 API", description = "사용자 관련 API")
public interface UserV1ApiSpec {

    @Operation(
            summary = "회원가입",
            description = "신규 회원을 등록합니다."
    )
    ApiResponse<UserResponse.Join> join(
            @RequestBody UserRequest.Join request
    );

    @Operation(
            summary = "회원조회",
            description = "로그인 ID로 회원을 조회합니다."
    )
    ApiResponse<UserResponse.GetUser> getUser(
            @Schema(name = "로그인 ID", description = "조회할 로그인 ID") String loginId
    );

    @Operation(
            summary = "내 정보 조회",
            description = "내 정보를 조회합니다."
    )
    ApiResponse<UserResponse.GetUser> getCurrentUser(
            @Schema(name = ApiHeader.X_USER_ID, description = "사용자 로그인 ID") String loginId
    );

}
