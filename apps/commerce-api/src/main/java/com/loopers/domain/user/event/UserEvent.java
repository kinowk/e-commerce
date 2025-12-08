package com.loopers.domain.user.event;

import com.loopers.domain.saga.event.SagaEvent;
import com.loopers.domain.user.User;

import java.util.UUID;

public class UserEvent {

    public record Join(UUID eventId, String eventName, Long userId) implements SagaEvent {
        public static UserEvent.Join from(User user) {
            return new UserEvent.Join(
                    UUID.randomUUID(),
                    "User.Join",
                    user.getId()
            );
        }
    }
}
