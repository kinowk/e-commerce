package com.loopers.domain.like;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCacheRepository;
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
    private ProductCacheRepository productCacheRepository;

    @DisplayName("좋아요 추가 시")
    @Nested
    class AddLike {

        @DisplayName("상품이 존재하지 않으면, NOT_FOUND 에러가 발생한다.")
        @Test
        void throwsException_whenProductNotFound() {
            // arrange
            LikeCommand.Toggle command = new LikeCommand.Toggle(1L, 1L);
            given(productRepository.findByIdForUpdate(anyLong())).willReturn(Optional.empty());

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

            given(productRepository.findByIdForUpdate(anyLong())).willReturn(Optional.of(product));
            given(likeRepository.existsByUserIdAndProductId(anyLong(), anyLong())).willReturn(true);

            // act
            LikeResult.Toggle result = likeService.addLike(command);

            // assert
            assertThat(result.likeCount()).isEqualTo(1L);
            verify(likeRepository, never()).save(any(Like.class));
            verify(productRepository, never()).save(any(Product.class));
        }

        @DisplayName("좋아요하지 않은 경우, 좋아요가 저장되고 좋아요 수가 증가한다.")
        @Test
        void savesLikeAndIncreases_whenNotYetLiked() {
            // arrange
            LikeCommand.Toggle command = new LikeCommand.Toggle(1L, 1L);
            Product product = new Product(1L, "상품명", "설명", 1000L, 10L);

            given(productRepository.findByIdForUpdate(anyLong())).willReturn(Optional.of(product));
            given(likeRepository.existsByUserIdAndProductId(anyLong(), anyLong())).willReturn(false);
            given(likeRepository.save(any(Like.class))).willAnswer(inv -> inv.getArgument(0));
            given(productRepository.save(any(Product.class))).willAnswer(inv -> inv.getArgument(0));

            // act
            LikeResult.Toggle result = likeService.addLike(command);

            // assert
            assertThat(result.likeCount()).isEqualTo(1L);
            verify(likeRepository).save(any(Like.class));
            verify(productRepository).save(any(Product.class));
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
            given(productRepository.findByIdForUpdate(anyLong())).willReturn(Optional.empty());

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

            given(productRepository.findByIdForUpdate(anyLong())).willReturn(Optional.of(product));
            given(likeRepository.existsByUserIdAndProductId(anyLong(), anyLong())).willReturn(false);

            // act
            LikeResult.Toggle result = likeService.removeLike(command);

            // assert
            assertThat(result.likeCount()).isEqualTo(0L);
            verify(likeRepository, never()).deleteByUserIdAndProductId(anyLong(), anyLong());
            verify(productRepository, never()).save(any(Product.class));
        }

        @DisplayName("좋아요한 경우, 좋아요가 삭제되고 좋아요 수가 감소한다.")
        @Test
        void deletesLikeAndDecreases_whenLiked() {
            // arrange
            LikeCommand.Toggle command = new LikeCommand.Toggle(1L, 1L);
            Product product = new Product(1L, "상품명", "설명", 1000L, 10L);
            product.increaseLikeCount();

            given(productRepository.findByIdForUpdate(anyLong())).willReturn(Optional.of(product));
            given(likeRepository.existsByUserIdAndProductId(anyLong(), anyLong())).willReturn(true);
            given(productRepository.save(any(Product.class))).willAnswer(inv -> inv.getArgument(0));

            // act
            LikeResult.Toggle result = likeService.removeLike(command);

            // assert
            assertThat(result.likeCount()).isEqualTo(0L);
            verify(likeRepository).deleteByUserIdAndProductId(anyLong(), anyLong());
            verify(productRepository).save(any(Product.class));
        }
    }
}
