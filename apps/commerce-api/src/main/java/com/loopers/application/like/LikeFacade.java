package com.loopers.application.like;

import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeResult;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LikeFacade {

    private final UserService userService;
    private final ProductService productService;
    private final LikeService likeService;

    public void like(LikeInput.Like input) {
        userService.getUser(input.userId());

        productService.getProductDetail(input.productId())
                .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "존재하지 않는 상품입니다."));

        LikeCommand.Like command = input.toCommand();
        likeService.like(command);
    }

    public void dislike(LikeInput.dislike input) {
        userService.getUser(input.userId());

        productService.getProductDetail(input.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));

        LikeCommand.Dislike command = input.toCommand();
        likeService.dislike(command);
    }

    public LikeOutput.LikeProducts getLikeProducts(Long userId) {
        userService.getUser(userId);

        LikeResult.GetLikeProducts result = likeService.getLikeProducts(userId);
        return LikeOutput.LikeProducts.from(result);
    }
}
