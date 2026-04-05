package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.attribute.ProductSortType;
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
    private final ProductCacheRepository productCacheRepository;

    @Transactional(readOnly = true)
    public ProductResult.Detail getProduct(Long productId) {
        return productCacheRepository.getDetail(productId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
                    Brand brand = brandRepository.findById(product.getBrandId())
                            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
                    ProductResult.Detail detail = ProductResult.Detail.of(product, brand);
                    productCacheRepository.putDetail(productId, detail);
                    return detail;
                });
    }

    @Transactional(readOnly = true)
    public ProductResult.List getProducts(ProductCommand.Query query) {
        String cacheKey = buildListCacheKey(query);
        return productCacheRepository.getList(cacheKey)
                .orElseGet(() -> {
                    List<Product> products = productRepository.findAll(query);

                    List<Long> brandIds = products.stream()
                            .map(Product::getBrandId)
                            .distinct()
                            .toList();
                    Map<Long, Brand> brandMap = brandRepository.findAllByIdIn(brandIds)
                            .stream()
                            .collect(Collectors.toMap(Brand::getId, b -> b));

                    ProductResult.List result = ProductResult.List.of(products, brandMap);
                    productCacheRepository.putList(cacheKey, result);
                    return result;
                });
    }

    @Transactional(readOnly = true)
    public Product getProductForOrder(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }

    @Transactional
    public Product saveProduct(Product product) {
        Product saved = productRepository.save(product);
        productCacheRepository.evictDetail(saved.getId());
        return saved;
    }

    private String buildListCacheKey(ProductCommand.Query query) {
        String brandId = query.brandId() != null ? String.valueOf(query.brandId()) : "all";
        String sortType = query.sortType() != null ? query.sortType().name() : ProductSortType.LATEST.name();
        return brandId + ":" + sortType + ":" + query.page() + ":" + query.size();
    }
}
