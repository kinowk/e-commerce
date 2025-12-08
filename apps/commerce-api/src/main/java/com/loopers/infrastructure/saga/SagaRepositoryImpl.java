package com.loopers.infrastructure.saga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.saga.Inbox;
import com.loopers.domain.saga.Outbox;
import com.loopers.domain.saga.SagaRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SagaRepositoryImpl implements SagaRepository {

    private final InboxJpaRepository inboxJpaRepository;
    private final OutboxJpaRepository outboxJpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void save(Inbox inbox) {
        inboxJpaRepository.insertIfNotExists(inbox.getEventId(), inbox.getEventName(), serialize(inbox.getPayload()), inbox.getCreatedAt(), inbox.getUpdatedAt());
    }

    @Override
    public void save(Outbox outbox) {
        outboxJpaRepository.insertIfNotExists(outbox.getEventId(), outbox.getEventName(), serialize(outbox.getPayload()), outbox.getCreatedAt(), outbox.getUpdatedAt());
    }

    private String serialize(Map<String, Object> payload) {
        if (payload == null)
            return null;

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, e.getMessage());
        }
    }
}
