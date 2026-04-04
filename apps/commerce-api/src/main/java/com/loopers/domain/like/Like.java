package com.loopers.domain.like;

import com.loopers.domain.BaseTimeEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ref_user_id", "ref_product_id"})
)
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @Column(name = "ref_user_id", nullable = false)
    private Long userId;

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    public Like(Long userId, Long productId) {
        if (userId == null || productId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "userId와 productId는 필수입니다.");
        }
        this.userId = userId;
        this.productId = productId;
    }
}
