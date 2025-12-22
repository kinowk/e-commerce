package com.loopers.infrastructure.product.event;

import com.loopers.config.kafka.LoopersKafkaProperties;
import com.loopers.domain.kafka.KafkaMessage;
import com.loopers.domain.product.event.ProductEvent;
import com.loopers.domain.product.event.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventPublisherImpl implements ProductEventPublisher {

    private final LoopersKafkaProperties properties;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publishEvent(ProductEvent.Like event) {
        KafkaMessage<ProductEvent.Like> message = KafkaMessage.from(event);
        String partitionKey = event.productId().toString();
        kafkaTemplate.send(properties.getTopic(event), partitionKey, message);
    }

    @Override
    public void publishEvent(ProductEvent.Dislike event) {
        KafkaMessage<ProductEvent.Dislike> message = KafkaMessage.from(event);
        String partitionKey = event.productId().toString();
        kafkaTemplate.send(properties.getTopic(event), partitionKey, message);
    }

}
