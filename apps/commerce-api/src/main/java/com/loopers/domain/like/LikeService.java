package com.loopers.domain.like;

import com.loopers.domain.like.event.LikeToggledEvent;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 좋아요 저장 (핵심), 집계는 LikeEventHandler에서 eventual consistency로 처리
     */
    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public LikeResult.Toggle addLike(LikeCommand.Toggle command) {
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        if (likeRepository.existsByUserIdAndProductId(command.userId(), command.productId())) {
            return new LikeResult.Toggle(product.getId(), product.getLikeCount());
        }

        try {
            likeRepository.save(new Like(command.userId(), command.productId()));
        } catch (DataIntegrityViolationException e) {
            return new LikeResult.Toggle(product.getId(), product.getLikeCount());
        }

        eventPublisher.publishEvent(new LikeToggledEvent(command.userId(), command.productId(), true));
        return new LikeResult.Toggle(product.getId(), product.getLikeCount());
    }

    /**
     * 좋아요 취소 (핵심), 집계는 LikeEventHandler에서 eventual consistency로 처리
     */
    @Transactional
    public LikeResult.Toggle removeLike(LikeCommand.Toggle command) {
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        if (!likeRepository.existsByUserIdAndProductId(command.userId(), command.productId())) {
            return new LikeResult.Toggle(product.getId(), product.getLikeCount());
        }

        likeRepository.deleteByUserIdAndProductId(command.userId(), command.productId());
        eventPublisher.publishEvent(new LikeToggledEvent(command.userId(), command.productId(), false));
        return new LikeResult.Toggle(product.getId(), product.getLikeCount());
    }

    @Transactional(readOnly = true)
    public LikeResult.LikedProductIds getLikedProductIds(Long userId) {
        List<Long> productIds = likeRepository.findProductIdsByUserId(userId);
        return new LikeResult.LikedProductIds(productIds);
    }
}
