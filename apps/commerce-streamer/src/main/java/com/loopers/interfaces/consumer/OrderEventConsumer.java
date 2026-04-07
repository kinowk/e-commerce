package com.loopers.interfaces.consumer;

import com.loopers.application.OrderEventProcessor;
import com.loopers.config.kafka.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderEventProcessor processor;

    @KafkaListener(
            topics = {"${kafka.topics.order-events}"},
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(List<ConsumerRecord<String, byte[]>> records, Acknowledgment acknowledgment) {
        for (ConsumerRecord<String, byte[]> record : records) {
            try {
                processor.process(record);
            } catch (Exception e) {
                log.error("[OrderConsumer] 메시지 처리 실패 - offset: {}, partition: {}",
                        record.offset(), record.partition(), e);
            }
        }
        acknowledgment.acknowledge();
    }
}
