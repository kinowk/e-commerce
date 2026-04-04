package com.loopers.domain.brand;

import java.util.List;
import java.util.Optional;

public interface BrandRepository {
    Optional<Brand> findById(Long id);
    List<Brand> findAllByIdIn(List<Long> ids);
}
