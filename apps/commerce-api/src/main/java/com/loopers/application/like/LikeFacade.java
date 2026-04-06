package com.loopers.application.like;

import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeFacade {

    private final LikeService likeService;

    @Transactional
    public LikeOutput.Toggle addLike(LikeInput.Toggle input) {
        LikeCommand.Toggle command = input.toCommand();
        return LikeOutput.Toggle.from(likeService.addLike(command));
    }

    @Transactional
    public LikeOutput.Toggle removeLike(LikeInput.Toggle input) {
        LikeCommand.Toggle command = input.toCommand();
        return LikeOutput.Toggle.from(likeService.removeLike(command));
    }

    public LikeOutput.LikedProductIds getLikedProductIds(Long userId) {
        return LikeOutput.LikedProductIds.from(likeService.getLikedProductIds(userId));
    }
}
