package com.loopers.infrastructure.like.event;

import com.loopers.config.kafka.LoopersKafkaProperties;
import com.loopers.domain.kafka.KafkaMessage;
import com.loopers.domain.like.event.LikeEvent;
import com.loopers.domain.like.event.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisherImpl implements LikeEventPublisher {

    private final LoopersKafkaProperties properties;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publishEvent(LikeEvent.Like event) {
        KafkaMessage<LikeEvent.Like> message = KafkaMessage.from(event);
        String partitionKey = event.productId().toString();
        kafkaTemplate.send(properties.getTopic(event), partitionKey, message);
    }

    @Override
    public void publishEvent(LikeEvent.Dislike event) {
        KafkaMessage<LikeEvent.Dislike> message = KafkaMessage.from(event);
        String partitionKey = event.productId().toString();
        kafkaTemplate.send(properties.getTopic(event), partitionKey, message);
    }

}
