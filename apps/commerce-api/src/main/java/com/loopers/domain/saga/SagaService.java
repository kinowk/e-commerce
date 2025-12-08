package com.loopers.domain.saga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SagaService {

    private final SagaRepository sagaRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void inbound(SagaCommand.Inbound command) {
        Inbox inbox = Inbox.builder()
                .eventId(command.eventId())
                .eventName(command.eventName())
                .payload(serialize(command.payload()))
                .build();

        sagaRepository.save(inbox);
    }

    @Transactional
    public void outbound(SagaCommand.Outbound command) {
        Outbox outbox = Outbox.builder()
                .eventId(command.eventId())
                .eventName(command.eventName())
                .payload(serialize(command.payload()))
                .build();

        sagaRepository.save(outbox);
    }

    private String serialize(Object value) {
        if (value == null)
            return null;

        String serializedValue = null;
        try {
            serializedValue = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, e.getMessage());
        }

        return serializedValue;
    }

}
