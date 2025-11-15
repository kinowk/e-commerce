package com.loopers.interfaces.api.brand;

import com.loopers.domain.brand.BrandResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandResponse {

    public record GetBrand(Long id, String name, String description, ZonedDateTime createdAt, ZonedDateTime updatedAt, ZonedDateTime deletedAt) {
        public static GetBrand from(BrandResult.GetBrand result) {
            return new GetBrand(
                    result.getId(),
                    result.getName(),
                    result.getDescription(),
                    result.getCreatedAt(),
                    result.getUpdatedAt(),
                    result.getDeletedAt()
            );
        }
    }
}
