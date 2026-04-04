package com.loopers.domain.like;

import com.loopers.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_login_id", "product_id"})
)
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login_id", nullable = false)
    private String userLoginId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    public Like(String userLoginId, Long productId) {
        this.userLoginId = userLoginId;
        this.productId = productId;
    }
}
