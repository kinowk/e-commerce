package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class BrandTest {

    @DisplayName("브랜드 생성 시")
    @Nested
    class Create {

        @DisplayName("브랜드 이름이 유효하지 않으면, BAD REQUEST 에러가 발생한다.")
        @NullSource
        @ValueSource(strings = {
                "", " "
        })
        @ParameterizedTest
        void throwsException_whenInvalidBrandName(String brandName) {
            //given
            String description = "brand description";

            //when & then
            CoreException coreException = assertThrows(CoreException.class, () -> {
                Brand.builder()
                        .name(brandName)
                        .description(description)
                        .build();
            });

            assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("유효한 값이면, 브랜드를 생성한다.")
        @CsvSource(value = {
                "UGG|어그(UGG)는 캘리포니아 컨템포러리 라이프스타일 브랜드",
                "일광전구|1962년 창립 이래 60여 년 동안  백열전구만 생산해 온 일광전구",
                "Apple|\"\"",
                "스탠리(STANLEY)|"
        }, delimiterString = "|")
        @ParameterizedTest
        void returnsBrand_whenValidValue(String brandName, String description) {
            //when
            Brand brand = Brand.builder()
                    .name(brandName)
                    .description(description)
                    .build();

            //then
            assertThat(brand).isNotNull();
            assertThat(brand.getName()).isEqualTo(brandName);
            assertThat(brand.getDescription()).isEqualTo(description);
        }
    }
}
