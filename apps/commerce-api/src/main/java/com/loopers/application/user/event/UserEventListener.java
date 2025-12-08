package com.loopers.application.user.event;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.saga.SagaCommand;
import com.loopers.domain.saga.SagaService;
import com.loopers.domain.user.event.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final PointService pointService;
    private final SagaService sagaService;

    @TransactionalEventListener
    public void createUserPoint(UserEvent.Join event) {
        SagaCommand.Inbound inboundCommand = new SagaCommand.Inbound(
                event.eventId(),
                event.eventName(),
                event
        );
        sagaService.inbound(inboundCommand);

        PointCommand.Charge command = new PointCommand.Charge(event.userId(), 0L);
        pointService.charge(command);
    }
}
