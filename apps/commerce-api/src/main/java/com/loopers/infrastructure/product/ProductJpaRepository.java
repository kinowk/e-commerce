package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByBrandId(Long brandId, Pageable pageable);

    Page<Product> findAll(Pageable pageable);
}
