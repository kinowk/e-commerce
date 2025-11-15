package com.loopers.domain.like;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품을 좋아요 할 수 있다.")
    void like() {
        // given
        LikeCommand.Like command = new LikeCommand.Like(1L, 1L);
        when(likeRepository.findByUserIdAndProductId(any(), any())).thenReturn(Optional.empty());

        // when
        likeService.like(command);

        // then
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    @DisplayName("이미 좋아요 한 상품은 중복으로 좋아요 할 수 없다.")
    void like_already_liked() {
        // given
        LikeCommand.Like command = new LikeCommand.Like(1L, 1L);
        when(likeRepository.findByUserIdAndProductId(any(), any())).thenReturn(Optional.of(new Like(1L, 1L)));

        // when
        likeService.like(command);

        // then
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("상품을 좋아요 하면 상품의 좋아요 수가 증가한다.")
    void like_update_product_like_count() {
        // given
        LikeCommand.Like command = new LikeCommand.Like(1L, 1L);
        Product product = Product.builder().name("test").basePrice(1000).brandId(1L).build();
        when(likeRepository.findByUserIdAndProductId(any(), any())).thenReturn(Optional.empty());
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        // when
        likeService.like(command);

        // then
        verify(productRepository, times(1)).findById(1L);
        assertThat(product.getLikeCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("좋아요 한 상품을 취소할 수 있다.")
    void dislike() {
        // given
        LikeCommand.Dislike command = new LikeCommand.Dislike(1L, 1L);
        Like like = new Like(1L, 1L);
        when(likeRepository.findByUserIdAndProductId(any(), any())).thenReturn(Optional.of(like));

        // when
        likeService.dislike(command);

        // then
        verify(likeRepository, times(1)).delete(any(Like.class));
    }

    @Test
    @DisplayName("좋아요를 취소하면 상품의 좋아요 수가 감소한다.")
    void dislike_update_product_like_count() {
        // given
        LikeCommand.Dislike command = new LikeCommand.Dislike(1L, 1L);
        Product product = Product.builder().name("test").basePrice(1000).brandId(1L).build();
        product.like();
        when(likeRepository.findByUserIdAndProductId(any(), any())).thenReturn(Optional.of(new Like(1L, 1L)));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        // when
        likeService.dislike(command);

        // then
        verify(productRepository, times(1)).findById(1L);
        assertThat(product.getLikeCount()).isZero();
    }

    @Test
    @DisplayName("좋아요 하지 않은 상품은 취소할 수 없다.")
    void dislike_not_liked() {
        // given
        LikeCommand.Dislike command = new LikeCommand.Dislike(1L, 1L);
        when(likeRepository.findByUserIdAndProductId(any(), any())).thenReturn(Optional.empty());

        // when
        likeService.dislike(command);

        // then
        verify(likeRepository, never()).delete(any(Like.class));
    }
}
