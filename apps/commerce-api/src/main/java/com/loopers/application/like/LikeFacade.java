package com.loopers.application.like;

import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeFacade {

    private final LikeService likeService;

    public LikeOutput.Toggle addLike(LikeInput.Toggle input) {
        LikeCommand.Toggle command = input.toCommand();
        return LikeOutput.Toggle.from(likeService.addLike(command));
    }

    public LikeOutput.Toggle removeLike(LikeInput.Toggle input) {
        LikeCommand.Toggle command = input.toCommand();
        return LikeOutput.Toggle.from(likeService.removeLike(command));
    }

    public LikeOutput.LikedProductIds getLikedProductIds(Long userId) {
        return LikeOutput.LikedProductIds.from(likeService.getLikedProductIds(userId));
    }
}
