package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.application.like.LikeInput;
import com.loopers.application.like.LikeOutput;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/like")
public class LikeV1Controller implements LikeV1ApiSpec {

    private final LikeFacade likeFacade;

    @GetMapping("/products")
    @Override
    public ApiResponse<LikeResponse.GetLikeProducts> getLikeProducts(@RequestHeader("X-USER-ID") Long userId) {
        LikeOutput.LikeProducts output = likeFacade.getLikeProducts(userId);
        LikeResponse.GetLikeProducts response = LikeResponse.GetLikeProducts.from(output);
        return ApiResponse.success(response);
    }

    @PostMapping("/products/{productId}")
    @Override
    public ApiResponse<Boolean> like(@RequestHeader("X-USER-ID") Long userId, @PathVariable Long productId) {
        LikeInput.Like input = new LikeInput.Like(userId, productId);
        likeFacade.like(input);
        return ApiResponse.success(true);
    }

    @DeleteMapping("/products/{productId}")
    @Override
    public ApiResponse<Boolean> dislike(@RequestHeader("X-USER-ID") Long userId, @PathVariable Long productId) {
        LikeInput.dislike input = new LikeInput.dislike(userId, productId);
        likeFacade.dislike(input);
        return ApiResponse.success(true);
    }

}
