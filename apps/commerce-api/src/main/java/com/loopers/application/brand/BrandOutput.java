package com.loopers.application.brand;

import com.loopers.domain.brand.BrandResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandOutput {

    public record Get(Long id, String name, String description) {
        public static Get from(BrandResult.Get result) {
            return new Get(result.id(), result.name(), result.description());
        }
    }
}
