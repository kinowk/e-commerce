package com.loopers.domain.product;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Optional<Product> findById(Long id);

    Page<ProductQueryResult.Products> getProductSummary(ProductQueryCommand.GetProductSummary command);

    Optional<ProductQueryResult.ProductOptions> findProductOptionsByIds(List<Long> productOptionIds);

    List<Stock> findStocksForUpdate(List<Long> productOptionIds);

    Product save(Product product);

    List<Stock> saveStocks(List<Stock> stocks);

    void increaseLikeCount(Long id);

    void decreaseLikeCount(Long id);
}
