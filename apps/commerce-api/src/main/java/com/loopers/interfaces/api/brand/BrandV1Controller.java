package com.loopers.interfaces.api.brand;

import com.loopers.domain.brand.BrandResult;
import com.loopers.domain.brand.BrandService;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/brands")
public class BrandV1Controller implements BrandV1ApiSpec {

    private final BrandService brandService;

    @GetMapping("/{brandId}")
    @Override
    public ApiResponse<BrandResponse.GetBrand> getBrand(@PathVariable Long brandId) {
        BrandResult.GetBrand result = brandService.findBrand(brandId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "브랜드가 존재하지 않습니다."));
        BrandResponse.GetBrand.from(result);
        return null;
    }

}
