package com.loopers.domain.brand;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public Optional<BrandResult.GetBrand> findBrand(Long brandId) {
        if (brandId == null)
            return Optional.empty();

        return brandRepository.findById(brandId)
                .map(BrandResult.GetBrand::from);
    }

}
