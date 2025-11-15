package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public PointResult.GetPoint getPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .map(PointResult.GetPoint::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

    }

    @Transactional
    public PointResult.Charge charge(PointCommand.Charge command) {
        Long userId = command.userId();
        Long amount = command.amount();

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));

        point.charge(amount);

        pointRepository.save(point);

        PointHistory history = PointHistory.builder()
                .userId(userId)
                .amount(amount)
                .transactionType(PointTransactionType.CHARGE)
                .build();

        pointRepository.saveHistory(history);

        return PointResult.Charge.from(point);
    }
}
