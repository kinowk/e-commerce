package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LikeTest {

    @DisplayName("좋아요 생성 시")
    @Nested
    class Create {

        @DisplayName("사용자 ID가 유효하지 않으면, BAD REQUEST 에러가 발생한다.")
        @NullSource
        @ParameterizedTest
        void throwsException_whenInvalidUserId(Long userId) {
            //given
            Long productId = 10L;

            //when & then
            CoreException coreException = assertThrows(CoreException.class, () -> {
                Like.builder()
                        .userId(userId)
                        .productId(productId)
                        .build();
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("상품 ID가 유효하지 않으면, BAD REQUEST 에러가 발생한다.")
        @NullSource
        @ParameterizedTest
        void throwsException_whenInvalidProductId(Long productId) {
            //given
            Long userId = 1L;

            //when & then
            CoreException coreException = assertThrows(CoreException.class, () -> {
                Like.builder()
                        .userId(userId)
                        .productId(productId)
                        .build();
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("유효한 값이면, 좋아요를 생성한다.")
        @CsvSource(value = {
                "1|1",
                "111|111",
                "99999|123"
        }, delimiterString = "|"
        )
        @ParameterizedTest
        void returnsLike_whenValidValues(Long userId, Long productId) {
            //when
            Like like = Like.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();

            //then
            assertThat(like).isNotNull();
            assertThat(like.getUserId()).isEqualTo(userId);
            assertThat(like.getProductId()).isEqualTo(productId);
        }
    }
}
