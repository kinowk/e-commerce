package com.loopers.domain.like.event;

public interface LikeEventPublisher {

    void publishEvent(LikeEvent.Like event);

    void publishEvent(LikeEvent.Dislike event);

}
