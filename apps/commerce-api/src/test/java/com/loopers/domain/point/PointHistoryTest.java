package com.loopers.domain.point;

import com.loopers.domain.point.attribute.PointHistoryType;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointHistoryTest {

    @DisplayName("포인트 히스토리 객체 생성 시")
    @Nested
    class Create {

        @DisplayName("금액이 null인 경우, 400 에러를 발생한다")
        @Test
        void throwsException_whenAmountIsNull() {
            // given
            Long pointId = 1L;
            Long userId = 1L;
            Long amount = null;
            PointHistoryType pointHistoryType = PointHistoryType.USE;
            String description = "상품 구매 시 포인트 사용";

            // when & then
            assertThatThrownBy(() -> new PointHistory(pointId, userId, amount, pointHistoryType, description))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("설명이 null인 경우, 400 에러를 발생한다")
        @Test
        void throwsException_whenDescriptionIsNull() {
            // given
            Long pointId = 1L;
            Long userId = 1L;
            Long amount = null;
            PointHistoryType pointHistoryType = PointHistoryType.USE;
            String description = null;

            // when & then
            assertThatThrownBy(() -> new PointHistory(pointId, userId, amount, pointHistoryType, description))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("설명이 Empty인 경우, 400 에러를 발생한다")
        @Test
        void throwsException_whenDescriptionIsEmpty() {
            // given
            Long pointId = 1L;
            Long userId = 1L;
            Long amount = null;
            PointHistoryType pointHistoryType = PointHistoryType.USE;
            String description = "";

            // when & then
            assertThatThrownBy(() -> new PointHistory(pointId, userId, amount, pointHistoryType, description))
                    .isInstanceOf(CoreException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
