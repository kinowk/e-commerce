package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@Entity
@Table(name = "brands")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "brand_name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Builder
    private Brand(String name, String description) {
        if (!StringUtils.hasText(name))
            throw new CoreException(ErrorType.BAD_REQUEST, "브랜드 이름이 올바르지 않습니다.");

        this.name = name;
        this.description = description;
    }
}
