package com.loopers.application.product;

import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    public ProductOutput.Detail getProduct(Long productId) {
        return ProductOutput.Detail.from(productService.getProduct(productId));
    }

    public ProductOutput.List getProducts(ProductInput.Query input) {
        return ProductOutput.List.from(productService.getProducts(input.toCommand()));
    }
}
