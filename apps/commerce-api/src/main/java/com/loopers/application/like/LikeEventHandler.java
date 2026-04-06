package com.loopers.application.like;

import com.loopers.domain.like.event.LikeToggledEvent;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCacheRepository;
import com.loopers.domain.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventHandler {

    private final ProductRepository productRepository;
    private final ProductCacheRepository productCacheRepository;

    /**
     * 좋아요 토글 후 상품 집계(like_count) 업데이트
     * 집계 실패가 좋아요 처리에 영향을 주지 않도록 별도 트랜잭션으로 처리
     */
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Async("eventExecutor")
    @Transactional
    public void handleLikeToggled(LikeToggledEvent event) {
        try {
            Product product = productRepository.findByIdForUpdate(event.productId())
                    .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

            if (event.added()) {
                product.increaseLikeCount();
            } else {
                product.decreaseLikeCount();
            }

            productRepository.save(product);
            productCacheRepository.evictDetail(event.productId());
            log.debug("[LikeEventHandler] 좋아요 집계 완료 - productId: {}, added: {}", event.productId(), event.added());
        } catch (Exception e) {
            log.error("[LikeEventHandler] 좋아요 집계 실패 - productId: {}, reason: {}", event.productId(), e.getMessage(), e);
        }
    }
}
