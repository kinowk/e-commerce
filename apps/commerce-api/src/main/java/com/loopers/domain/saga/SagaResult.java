package com.loopers.domain.saga;

import jakarta.annotation.Nullable;

import java.util.Map;

public record SagaResult() {

    public record Inbound(String eventId, String eventName, @Nullable Map<String, Object> payload, boolean saved) {
        public static Inbound from(Inbox inbox, boolean saved) {
            return new Inbound(
                    inbox.getEventId(),
                    inbox.getEventName(),
                    inbox.getPayload(),
                    saved
            );
        }
    }

    public record Outbound(String eventId, String eventName, @Nullable Map<String, Object> payload, boolean saved) {
        public static Outbound from(Outbox outbox, boolean saved) {
            return new Outbound(
                    outbox.getEventId(),
                    outbox.getEventName(),
                    outbox.getPayload(),
                    saved
            );
        }
    }

}
