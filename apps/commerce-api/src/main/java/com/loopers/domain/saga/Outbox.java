package com.loopers.domain.saga;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import io.hypersistence.utils.hibernate.type.json.JsonStringType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;

@Getter
@Entity
@Table(name = "outboxes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "event_id", nullable = false, updatable = false)
    private String eventId;

    @Column(name = "event_name", nullable = false, updatable = false)
    private String eventName;

    @Type(JsonStringType.class)
    @Column(name = "payload", nullable = false)
    private Map<String, Object> payload;

    @Builder
    private Outbox(String eventId, String eventName, Map<String, Object> payload) {
        if (!StringUtils.hasText(eventId))
            throw new CoreException(ErrorType.BAD_REQUEST, "이벤트ID가 올바르지 않습니다");

        if (!StringUtils.hasText(eventName))
            throw new CoreException(ErrorType.BAD_REQUEST, "이벤트명이 올바르지 않습니다.");

        this.eventId = eventId;
        this.eventName = eventName;
        this.payload = payload == null ? null : Collections.unmodifiableMap(payload);
    }

}

