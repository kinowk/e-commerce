package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.application.like.LikeInput;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/likes")
public class LikeV1Controller implements LikeV1ApiSpec {

    private final LikeFacade likeFacade;

    @PostMapping("/products/{productId}")
    @Override
    public ApiResponse<LikeResponse.Toggle> addLike(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long productId
    ) {
        LikeInput.Toggle input = new LikeInput.Toggle(userId, productId);
        return ApiResponse.success(LikeResponse.Toggle.from(likeFacade.addLike(input)));
    }

    @DeleteMapping("/products/{productId}")
    @Override
    public ApiResponse<LikeResponse.Toggle> removeLike(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long productId
    ) {
        LikeInput.Toggle input = new LikeInput.Toggle(userId, productId);
        return ApiResponse.success(LikeResponse.Toggle.from(likeFacade.removeLike(input)));
    }

    @GetMapping
    @Override
    public ApiResponse<LikeResponse.LikedProductIds> getLikedProductIds(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return ApiResponse.success(LikeResponse.LikedProductIds.from(likeFacade.getLikedProductIds(userId)));
    }
}
