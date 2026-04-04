package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.product.attribute.ProductSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public Optional<Product> findByIdForUpdate(Long id) {
        return productJpaRepository.findByIdForUpdate(id);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public List<Product> findAll(ProductCommand.Query query) {
        Sort sort = buildSort(query.sortType());
        Pageable pageable = PageRequest.of(query.page(), query.size(), sort);

        if (query.brandId() != null) {
            return productJpaRepository.findAllByBrandId(query.brandId(), pageable).getContent();
        }
        return productJpaRepository.findAll(pageable).getContent();
    }

    private Sort buildSort(ProductSortType sortType) {
        if (sortType == null) {
            return Sort.by("createdAt").descending();
        }
        return switch (sortType) {
            case PRICE_ASC -> Sort.by("price").ascending();
            case LIKES_DESC -> Sort.by("likeCount").descending();
            default -> Sort.by("createdAt").descending();
        };
    }
}
