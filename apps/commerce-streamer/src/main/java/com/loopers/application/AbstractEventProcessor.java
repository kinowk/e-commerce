package com.loopers.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class AbstractEventProcessor {

    protected final EventIdempotencyService idempotencyService;
    protected final ObjectMapper objectMapper;

    protected AbstractEventProcessor(EventIdempotencyService idempotencyService, ObjectMapper objectMapper) {
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper;
    }

    protected abstract String consumerName();

    protected abstract void handleEvent(String eventType, JsonNode payload);

    @Transactional
    public void process(ConsumerRecord<String, byte[]> record) throws Exception {
        JsonNode message = objectMapper.readTree(record.value());
        String eventId = message.get("eventId").asText();
        String eventType = message.get("eventType").asText();

        if (idempotencyService.isAlreadyHandled(eventId)) {
            log.debug("[{}] 이미 처리된 이벤트 - eventId: {}", consumerName(), eventId);
            return;
        }

        handleEvent(eventType, message.get("payload"));

        idempotencyService.markHandled(eventId, eventType);
        log.info("[{}] 이벤트 처리 완료 - eventId: {}, eventType: {}", consumerName(), eventId, eventType);
    }
}
