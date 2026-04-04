package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandOutput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandResponse {

    public record Get(Long id, String name, String description) {
        public static Get from(BrandOutput.Get output) {
            return new Get(output.id(), output.name(), output.description());
        }
    }
}
