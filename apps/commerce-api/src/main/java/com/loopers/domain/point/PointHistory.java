package com.loopers.domain.point;

import com.loopers.domain.BaseTimeEntity;
import com.loopers.domain.point.attribute.PointHistoryType;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_histories")
public class PointHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_point_id", nullable = false)
    private Long pointId;

    @Column(name = "ref_user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PointHistoryType type;

    @Column(name = "description", nullable = false)
    private String description;

    public PointHistory(Long pointId, Long userId, Long amount, PointHistoryType type, String description) {
        validateAmount(amount);
        validateDescription(description);

        this.pointId = pointId;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.description = description;
    }

    private void validateAmount(Long amount) {
        if (amount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }
    }

    private void validateDescription(String description) {
        if (!StringUtils.hasText(description)) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }
    }
}
