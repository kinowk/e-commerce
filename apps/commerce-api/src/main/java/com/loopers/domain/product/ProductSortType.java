package com.loopers.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductSortType {
    LATEST("최신순"),
    PRICE_ASC("가격순"),
    LIKES_DESC("인기순");

    private final String description;
}
