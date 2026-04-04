package com.loopers.domain.point;

import com.loopers.domain.point.attribute.PointHistoryType;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public PointResult.GetPoint getPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .map(point -> PointResult.GetPoint.from(userId, point))
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }

    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public PointResult.Charge charge(PointCommand.Charge command) {
        Long userId = command.userId();
        Long amount = command.amount();

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        point.charge(amount);
        Point savedPoint = pointRepository.save(point);

        PointHistory pointHistory = new PointHistory(savedPoint.getId(), savedPoint.getUserId(), amount, PointHistoryType.EARN, "포인트 충전");
        pointRepository.save(pointHistory);

        return new PointResult.Charge(userId, amount, savedPoint.getBalance());
    }
}
