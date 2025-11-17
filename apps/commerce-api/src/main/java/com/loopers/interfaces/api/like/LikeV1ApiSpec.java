package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Like V1 API", description = "좋아요 관련 API")
public interface LikeV1ApiSpec {

    @Operation(
            summary = "좋아요 한 상품 목록 조회",
            description = "사용자가 좋아요 한 상품 목록을 조회합니다."
    )
    ApiResponse<LikeResponse.GetLikeProducts> getLikeProducts(
            @Schema(name = "X-USER-ID", description = "사용자 ID") Long userId
    );

    @Operation(
            summary = "상품 좋아요 등록",
            description = "상품 좋아요를 등록합니다."
    )
    ApiResponse<Boolean> like(
            @Schema(name = "X-USER-ID", description = "사용자 ID") Long userId,
            @Schema(name = "상품 ID", description = "좋아요 할 상품 ID") Long productId
    );

    @Operation(
            summary = "상품 좋아요 취소",
            description = "상품 좋아요를 취소합니다,"
    )
    ApiResponse<Boolean> dislike(
            @Schema(name = "X-USER-ID", description = "사용자 ID") Long userId,
            @Schema(name = "상품 ID", description = "좋아요 할 상품 ID") Long productId
    );


}
