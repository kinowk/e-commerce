package com.loopers.interfaces.metric;

import java.time.LocalDateTime;

public record ProductRankingEvent(Long productId, String type, Long price, Long amount, LocalDateTime eventTime) {
}
