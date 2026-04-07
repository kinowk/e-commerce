package com.loopers.application.outbox;

import com.loopers.domain.outbox.OutboxEvent;
import com.loopers.domain.outbox.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OutboxRelayScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final int batchSize;

    public OutboxRelayScheduler(
            OutboxRepository outboxRepository,
            KafkaTemplate<Object, Object> kafkaTemplate,
            @Value("${kafka.outbox.batch-size:100}") int batchSize
    ) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${kafka.outbox.poll-interval-ms:3000}")
    @Transactional
    public void relay() {
        List<OutboxEvent> events = outboxRepository.findUnpublished(batchSize);
        if (events.isEmpty()) {
            return;
        }

        int published = 0;
        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getPartitionKey(), event.getPayload())
                        .get(5, TimeUnit.SECONDS);
                event.markPublished();
                published++;
            } catch (Exception e) {
                log.error("[OutboxRelay] Kafka 발행 실패 - eventId: {}, topic: {}",
                        event.getId(), event.getTopic(), e);
            }
        }

        log.info("[OutboxRelay] {} 건 발행 완료", published);
    }
}
