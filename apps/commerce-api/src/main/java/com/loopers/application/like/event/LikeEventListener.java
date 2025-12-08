package com.loopers.application.like.event;

import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.event.LikeEvent;
import com.loopers.domain.product.ProductService;
import com.support.annotation.Inboxing;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LikeEventListener {

    private final ProductService productService;

    @Async
    @Inboxing
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void likeProduct(LikeEvent.Like event) {
        productService.likeProduct(event.productId());
    }

    @Async
    @Inboxing
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dislikeProduct(LikeEvent.Dislike event) {
        productService.dislikeProduct(event.productId());
    }
}
