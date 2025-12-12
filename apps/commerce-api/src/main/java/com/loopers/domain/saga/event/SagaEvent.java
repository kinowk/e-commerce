package com.loopers.domain.saga.event;

public interface SagaEvent {

    String eventId();

    String eventName();
}
