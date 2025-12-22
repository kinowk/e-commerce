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

    private final ApplicationContext applicationContext;

    @Override
    public void publishEvent(@NonNull Object event) {
        if (event instanceof SagaEvent sagaEvent) {
            Runnable runnable = () -> {
                SagaCommand.Outbound outbound = new SagaCommand.Outbound(
                        sagaEvent.eventId(),
                        sagaEvent.eventName(),
                        sagaEvent
                );
            };
            if (TransactionSynchronizationManager.isSynchronizationActive() && TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void beforeCommit(boolean readOnly) {
                        runnable.run();
                    }
                });
            }
        }

        applicationContext.publishEvent(event);
    }
}
