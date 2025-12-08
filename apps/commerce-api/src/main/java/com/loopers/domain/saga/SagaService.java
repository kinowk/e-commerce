package com.loopers.domain.saga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SagaService {

    private final SagaRepository sagaRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public SagaResult.Inbound inbound(SagaCommand.Inbound command) {
        Inbox inbox = Inbox.builder()
                .eventId(command.eventId())
                .eventName(command.eventName())
                .payload(serialize(command.payload()))
                .build();

        sagaRepository.save(inbox);

        return SagaResult.Inbound.from(inbox);
    }

    @Transactional
    public SagaResult.Outbound outbound(SagaCommand.Outbound command) {
        Outbox outbox = Outbox.builder()
                .eventId(command.eventId())
                .eventName(command.eventName())
                .payload(serialize(command.payload()))
                .build();

        sagaRepository.save(outbox);

        return SagaResult.Outbound.from(outbox);
    }

    private Map<String, Object> serialize(Object payload) {
        if (payload == null)
            return null;

        if (payload instanceof Map map) {
            return Collections.unmodifiableMap(map);
        }

        return objectMapper.convertValue(payload, new TypeReference<Map<String, Object>>() {
        });
    }

}
