package com.loopers.infrastructure.saga;

import com.loopers.domain.saga.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface InboxJpaRepository extends JpaRepository<Inbox, Long> {

    @Modifying
    @Query("""
            insert into inbox (eventId, eventName, payload, createdAt, updatedAt)
            values (:eventId, :eventName, :payload, :createdAy, :updatedAt)
            on conflict (eventId, eventName) do noting
            """)
    int insertIfNotExists(
            @Param("eventId") UUID eventId,
            @Param("eventName") String eventName,
            @Param("payload") String payload,
            @Param("createdAt") ZonedDateTime createdAt,
            @Param("updatedAt") ZonedDateTime updatedAt
    );
}
