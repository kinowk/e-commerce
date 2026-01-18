package com.loopers.domain.point;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    public PointResult.GetPoint getPoint(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        return pointRepository.findByUserId(user.getId())
                .map(point -> PointResult.GetPoint.from(loginId, point))
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }

    public PointResult.Charge charge(PointCommand.Charge command) {
        String loginId = command.loginId();
        Long amount = command.amount();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        Point point = pointRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

        point.charge(amount);
        Point savedPoint = pointRepository.save(point);
        return new PointResult.Charge(loginId, amount, savedPoint.getBalance());
    }
}
