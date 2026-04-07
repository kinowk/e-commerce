package com.loopers.domain.outbox;

import java.time.ZonedDateTime;
import java.util.Map;

public record KafkaEventMessage(
        String eventId,
        String eventType,
        String aggregateType,
        Long aggregateId,
        Map<String, Object> payload,
        ZonedDateTime occurredAt
) {
}
