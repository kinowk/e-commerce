package com.loopers.domain.point.attribute;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PointHistoryType {

    EARN("적립"),
    USE("사용"),
    EXPIRE("만료"),
    ADJUST("관리자 조정");

    private final String description;

}
