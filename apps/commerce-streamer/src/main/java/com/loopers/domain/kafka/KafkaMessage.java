package com.loopers.domain.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public record KafkaMessage<T>(
        String eventId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        ZonedDateTime publishedAt,
        T payload
) {
}
