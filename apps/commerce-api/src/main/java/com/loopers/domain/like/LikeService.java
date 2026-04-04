package com.loopers.domain.like;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;

    @Transactional
    public LikeResult.Toggle addLike(LikeCommand.Toggle command) {
        Product product = productRepository.findByIdForUpdate(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        if (likeRepository.existsByUserLoginIdAndProductId(command.userLoginId(), command.productId())) {
            return new LikeResult.Toggle(product.getId(), product.getLikeCount());
        }

        likeRepository.save(new Like(command.userLoginId(), command.productId()));
        product.increaseLikeCount();
        productRepository.save(product);

        return new LikeResult.Toggle(product.getId(), product.getLikeCount());
    }

    @Transactional
    public LikeResult.Toggle removeLike(LikeCommand.Toggle command) {
        Product product = productRepository.findByIdForUpdate(command.productId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        if (!likeRepository.existsByUserLoginIdAndProductId(command.userLoginId(), command.productId())) {
            return new LikeResult.Toggle(product.getId(), product.getLikeCount());
        }

        likeRepository.deleteByUserLoginIdAndProductId(command.userLoginId(), command.productId());
        product.decreaseLikeCount();
        productRepository.save(product);

        return new LikeResult.Toggle(product.getId(), product.getLikeCount());
    }

    @Transactional(readOnly = true)
    public LikeResult.LikedProductIds getLikedProductIds(String userLoginId) {
        List<Long> productIds = likeRepository.findProductIdsByUserLoginId(userLoginId);
        return new LikeResult.LikedProductIds(productIds);
    }
}
