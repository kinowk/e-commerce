package com.loopers.infrastructure.saga;

import com.loopers.domain.saga.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {

    @Modifying
    @Query("""
            insert into outbox (eventId, eventName, payload, createdAt, updatedAt)
            values (:eventId, :eventName, :payload, :createdAt, :updatedAt
            """)
    int insertIfNotExists(
            @Param("eventId") String eventId,
            @Param("eventName") String eventName,
            @Param("payload") String payload,
            @Param("createdAt") ZonedDateTime createdAt,
            @Param("updatedAt") ZonedDateTime updatedAt
    );
}
