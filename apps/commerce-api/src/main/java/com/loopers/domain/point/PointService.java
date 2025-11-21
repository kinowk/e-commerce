package com.loopers.domain.point;

import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
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

        userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자가 존재하지 않습니다."));

        Point point = pointRepository.findByUserId(userId)
                .orElse(Point.builder()
                        .userId(userId)
                        .balance(0L)
                        .build()
                );

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

    @Transactional
    public PointResult.Use use(PointCommand.Use command) {
        Long userId = command.userId();
        Long amount = command.amount();

        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        point.use(amount);
        Point savedPoint = pointRepository.save(point);

        PointHistory pointHistory = PointHistory.builder()
                .userId(userId)
                .amount(amount)
                .transactionType(PointTransactionType.USE)
                .build();

        pointRepository.saveHistory(pointHistory);

        return PointResult.Use.from(savedPoint);
    }
}
