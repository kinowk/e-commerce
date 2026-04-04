package com.loopers.application.brand;

import com.loopers.domain.brand.BrandResult;
import com.loopers.domain.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrandFacade {

    private final BrandService brandService;

    public BrandOutput.Get getBrand(Long brandId) {
        BrandResult.Get result = brandService.getBrand(brandId);
        return BrandOutput.Get.from(result);
    }
}
