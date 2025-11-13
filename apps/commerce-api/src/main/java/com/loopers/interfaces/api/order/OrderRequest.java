package com.loopers.interfaces.api.order;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {
    public record Create(@NotEmpty List<Product> products) {

        public record Product(Long productOptionId, Integer quantity) {

        }
    }
}
