package com.loopers.application;

import com.loopers.domain.event.EventHandled;
import com.loopers.infrastructure.event.EventHandledJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventIdempotencyService {

    private final EventHandledJpaRepository eventHandledRepository;

    public boolean isAlreadyHandled(String eventId) {
        return eventHandledRepository.existsById(eventId);
    }

    public void markHandled(String eventId, String eventType) {
        eventHandledRepository.save(new EventHandled(eventId, eventType));
    }
}
