package com.loopers.domain.like;

import com.loopers.domain.like.event.LikeToggledEvent;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @DisplayName("좋아요 추가 시")
    @Nested
    class AddLike {

        @DisplayName("상품이 존재하지 않으면, NOT_FOUND 에러가 발생한다.")
        @Test
        void throwsException_whenProductNotFound() {
            // arrange
            LikeCommand.Toggle command = new LikeCommand.Toggle(1L, 1L);
            given(productRepository.findById(anyLong())).willReturn(Optional.empty());

            // act & assert
            assertThatThrownBy(() -> likeService.addLike(command))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("이미 좋아요한 경우, 좋아요가 중복 저장되지 않고 현재 상태를 반환한다.")
        @Test
        void returnsCurrentState_whenAlreadyLiked() {
            // arrange
            LikeCommand.Toggle command = new LikeCommand.Toggle(1L, 1L);
            Product product = new Product(1L, "상품명", "설명", 1000L, 10L);
            product.increaseLikeCount();

            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
            given(likeRepository.existsByUserIdAndProductId(anyLong(), anyLong())).willReturn(true);

            // act
            LikeResult.Toggle result = likeService.addLike(command);

            // assert
            assertThat(result.likeCount()).isEqualTo(1L);
            verify(likeRepository, never()).save(any(Like.class));
            verify(eventPublisher, never()).publishEvent(any(LikeToggledEvent.class));
        }

        @DisplayName("좋아요하지 않은 경우, 좋아요가 저장되고 집계 이벤트가 발행된다.")
        @Test
        void savesLikeAndPublishesEvent_whenNotYetLiked() {
            // arrange
            LikeCommand.Toggle command = new LikeCommand.Toggle(1L, 1L);
            Product product = new Product(1L, "상품명", "설명", 1000L, 10L);

            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
            given(likeRepository.existsByUserIdAndProductId(anyLong(), anyLong())).willReturn(false);
            given(likeRepository.save(any(Like.class))).willAnswer(inv -> inv.getArgument(0));

            // act
            LikeResult.Toggle result = likeService.addLike(command);

            // assert: 좋아요 저장 + 이벤트 발행 (집계는 eventual consistency)
            assertThat(result.likeCount()).isEqualTo(0L); // 집계 전 현재 상태 반환
            verify(likeRepository).save(any(Like.class));
            verify(eventPublisher).publishEvent(any(LikeToggledEvent.class));
        }
    }

    @DisplayName("좋아요 취소 시")
    @Nested
    class RemoveLike {

        @DisplayName("상품이 존재하지 않으면, NOT_FOUND 에러가 발생한다.")
        @Test
        void throwsException_whenProductNotFound() {
            // arrange
            LikeCommand.Toggle command = new LikeCommand.Toggle(1L, 1L);
            given(productRepository.findById(anyLong())).willReturn(Optional.empty());

            // act & assert
            assertThatThrownBy(() -> likeService.removeLike(command))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);
        }

        @DisplayName("좋아요하지 않은 경우, 삭제 없이 현재 상태를 반환한다.")
        @Test
        void returnsCurrentState_whenNotLiked() {
            // arrange
            LikeCommand.Toggle command = new LikeCommand.Toggle(1L, 1L);
            Product product = new Product(1L, "상품명", "설명", 1000L, 10L);

            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
            given(likeRepository.existsByUserIdAndProductId(anyLong(), anyLong())).willReturn(false);

            // act
            LikeResult.Toggle result = likeService.removeLike(command);

            // assert
            assertThat(result.likeCount()).isEqualTo(0L);
            verify(likeRepository, never()).deleteByUserIdAndProductId(anyLong(), anyLong());
            verify(eventPublisher, never()).publishEvent(any(LikeToggledEvent.class));
        }

        @DisplayName("좋아요한 경우, 좋아요가 삭제되고 집계 이벤트가 발행된다.")
        @Test
        void deletesLikeAndPublishesEvent_whenLiked() {
            // arrange
            LikeCommand.Toggle command = new LikeCommand.Toggle(1L, 1L);
            Product product = new Product(1L, "상품명", "설명", 1000L, 10L);
            product.increaseLikeCount();

            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
            given(likeRepository.existsByUserIdAndProductId(anyLong(), anyLong())).willReturn(true);

            // act
            LikeResult.Toggle result = likeService.removeLike(command);

            // assert: 좋아요 삭제 + 이벤트 발행 (집계는 eventual consistency)
            assertThat(result.likeCount()).isEqualTo(1L); // 집계 전 현재 상태 반환
            verify(likeRepository).deleteByUserIdAndProductId(anyLong(), anyLong());
            verify(eventPublisher).publishEvent(any(LikeToggledEvent.class));
        }
    }
}
