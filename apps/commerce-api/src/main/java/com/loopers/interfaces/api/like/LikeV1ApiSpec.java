package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Like V1 API", description = "좋아요 관련 API")
public interface LikeV1ApiSpec {

    @Operation(
            summary = "좋아요 추가",
            description = "상품에 좋아요를 추가합니다. 이미 좋아요한 경우 현재 상태를 반환합니다."
    )
    ApiResponse<LikeResponse.Toggle> addLike(String userLoginId, Long productId);

    @Operation(
            summary = "좋아요 취소",
            description = "상품의 좋아요를 취소합니다. 좋아요하지 않은 경우 현재 상태를 반환합니다."
    )
    ApiResponse<LikeResponse.Toggle> removeLike(String userLoginId, Long productId);

    @Operation(
            summary = "좋아요한 상품 목록 조회",
            description = "사용자가 좋아요한 상품 ID 목록을 조회합니다."
    )
    ApiResponse<LikeResponse.LikedProductIds> getLikedProductIds(String userLoginId);
}
