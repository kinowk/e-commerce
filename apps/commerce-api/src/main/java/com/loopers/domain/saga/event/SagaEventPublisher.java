package com.loopers.domain.saga.event;

import com.loopers.domain.saga.SagaCommand;
import com.loopers.domain.saga.SagaService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Primary
@Component
@RequiredArgsConstructor
public class SagaEventPublisher implements ApplicationEventPublisher {

    private final SagaService sagaService;
    private final ApplicationContext applicationContext;

    @Override
    public void publishEvent(@NonNull Object event) {
        if (event instanceof SagaEvent sagaEvent) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    SagaCommand.Outbound command = new SagaCommand.Outbound(
                            sagaEvent.eventId(),
                            sagaEvent.eventName(),
                            sagaEvent
                    );

                    sagaService.outbound(command);
                }
            });
        }

        applicationContext.publishEvent(event);
    }
}
