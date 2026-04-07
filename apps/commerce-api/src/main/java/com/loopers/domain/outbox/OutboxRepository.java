package com.loopers.domain.outbox;

import java.util.List;

public interface OutboxRepository {
    OutboxEvent save(OutboxEvent event);
    List<OutboxEvent> findUnpublished(int limit);
    void saveAll(List<OutboxEvent> events);
}
