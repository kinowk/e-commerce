package com.loopers.domain.product;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductQueryCommand {

    public record GetProductSummary(String keyword, Long brandId, ProductSortType sortType, Integer page, Integer size) {
        public static GetProductSummary from(ProductCommand.GetProductSummary command) {
            return  new GetProductSummary(
                    command.keyword(),
                    command.brandId(),
                    command.sortType(),
                    command.page(),
                    command.size()
            );
        }
    }
}
