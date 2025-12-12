package com.loopers.domain.saga;

import jakarta.annotation.Nullable;

public record SagaCommand() {

    public record Inbound(String eventId, String eventName, @Nullable Object payload) {

    }

    public record Outbound(String eventId, String eventName, @Nullable Object payload) {

    }

}
