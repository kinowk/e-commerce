package com.loopers.application.point;

import com.loopers.domain.point.PointResult;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointFacade {

    private final UserService userService;
    private final PointService pointService;

    public PointOutput.Charge charge(PointInput.Charge input) {
        userService.findUser(input.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));

        PointResult.Charge result = pointService.charge(input.toCommand());
        return PointOutput.Charge.from(result);
    }

    public PointOutput.GetPoint getPoint(Long userId) {
        PointResult.GetPoint result = pointService.getPoint(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));

        return PointOutput.GetPoint.from(result);
    }
}
