package com.loopers.domain.like;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCacheRepository;
import com.loopers.domain.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final ProductCacheRepository productCacheRepository;

    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public LikeResult.Toggle addLike(LikeCommand.Toggle command) {
        Product product = productRepository.findByIdForUpdate(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        if (likeRepository.existsByUserIdAndProductId(command.userId(), command.productId())) {
            return new LikeResult.Toggle(product.getId(), product.getLikeCount());
        }

        try {
            likeRepository.save(new Like(command.userId(), command.productId()));
        } catch (DataIntegrityViolationException e) {
            return new LikeResult.Toggle(product.getId(), product.getLikeCount());
        }

        product.increaseLikeCount();
        productRepository.save(product);
        productCacheRepository.evictDetail(product.getId());

        return new LikeResult.Toggle(product.getId(), product.getLikeCount());
    }

    @Transactional
    public LikeResult.Toggle removeLike(LikeCommand.Toggle command) {
        Product product = productRepository.findByIdForUpdate(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        if (!likeRepository.existsByUserIdAndProductId(command.userId(), command.productId())) {
            return new LikeResult.Toggle(product.getId(), product.getLikeCount());
        }

        likeRepository.deleteByUserIdAndProductId(command.userId(), command.productId());
        product.decreaseLikeCount();
        productRepository.save(product);
        productCacheRepository.evictDetail(product.getId());

        return new LikeResult.Toggle(product.getId(), product.getLikeCount());
    }

    @Transactional(readOnly = true)
    public LikeResult.LikedProductIds getLikedProductIds(Long userId) {
        List<Long> productIds = likeRepository.findProductIdsByUserId(userId);
        return new LikeResult.LikedProductIds(productIds);
    }
}
