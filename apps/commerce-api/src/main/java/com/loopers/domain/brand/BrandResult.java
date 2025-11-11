package com.loopers.domain.brand;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandResult {

    @Getter
    @Builder
    public static class GetBrand {
        private final Long id;
        private final String name;
        private final String description;
        private final ZonedDateTime createdAt;
        private final ZonedDateTime updatedAt;
        private final ZonedDateTime deletedAt;

        public static GetBrand from(Brand brand) {
            return GetBrand.builder()
                    .id(brand.getId())
                    .name(brand.getName())
                    .description(brand.getDescription())
                    .createdAt(brand.getCreatedAt())
                    .updatedAt(brand.getUpdatedAt())
                    .deletedAt(brand.getDeletedAt())
                    .build();
        }
    }

}
