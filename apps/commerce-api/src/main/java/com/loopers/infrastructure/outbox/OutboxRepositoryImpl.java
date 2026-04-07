package com.loopers.infrastructure.outbox;

import com.loopers.domain.outbox.OutboxEvent;
import com.loopers.domain.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public OutboxEvent save(OutboxEvent event) {
        return outboxJpaRepository.save(event);
    }

    @Override
    public List<OutboxEvent> findUnpublished(int limit) {
        return outboxJpaRepository.findByPublishedFalseOrderByCreatedAtAsc(PageRequest.of(0, limit));
    }

    @Override
    public void saveAll(List<OutboxEvent> events) {
        outboxJpaRepository.saveAll(events);
    }
}
