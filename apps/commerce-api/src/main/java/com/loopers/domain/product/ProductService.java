package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public ProductResult.Detail getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
        Brand brand = brandRepository.findById(product.getBrandId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
        return ProductResult.Detail.of(product, brand);
    }

    @Transactional(readOnly = true)
    public ProductResult.List getProducts(ProductCommand.Query query) {
        List<Product> products = productRepository.findAll(query);

        List<Long> brandIds = products.stream()
                .map(Product::getBrandId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Brand> brandMap = brandRepository.findAllByIdIn(brandIds)
                .stream()
                .collect(Collectors.toMap(Brand::getId, b -> b));

        return ProductResult.List.of(products, brandMap);
    }

    @Transactional
    public Product getProductForOrder(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }

    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
