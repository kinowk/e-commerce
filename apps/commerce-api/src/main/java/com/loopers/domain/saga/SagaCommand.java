package com.loopers.domain.saga;

import jakarta.annotation.Nullable;

import java.util.UUID;

public record SagaCommand() {

    public record Inbound(UUID eventId, String eventName, @Nullable Object payload) {

    }

    public record Outbound(UUID eventId, String eventName, @Nullable Object payload) {

    }

}
