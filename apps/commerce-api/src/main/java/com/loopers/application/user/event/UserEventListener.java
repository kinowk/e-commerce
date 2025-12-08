package com.loopers.application.user.event;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.event.UserEvent;
import com.support.annotation.Inboxing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final PointService pointService;

    @Inboxing
    @TransactionalEventListener
    public void createUserPoint(UserEvent.Join event) {
        PointCommand.Charge command = new PointCommand.Charge(event.userId(), 0L);
        pointService.charge(command);
    }
}
