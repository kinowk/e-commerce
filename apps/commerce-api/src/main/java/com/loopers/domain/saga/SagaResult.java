package com.loopers.domain.saga;

import jakarta.annotation.Nullable;

import java.util.Map;
import java.util.UUID;

public record SagaResult() {

    public record Inbound(UUID eventId, String eventName, @Nullable Map<String, Object> payload) {
        public static Inbound from(Inbox inbox) {
            return new Inbound(
                    inbox.getEventId(),
                    inbox.getEventName(),
                    inbox.getPayload()
            );
        }
    }

    public record Outbound(UUID eventId, String eventName, @Nullable Map<String, Object> payload) {
        public static Outbound from(Outbox outbox) {
            return new Outbound(
                    outbox.getEventId(),
                    outbox.getEventName(),
                    outbox.getPayload()
            );
        }
    }

}
