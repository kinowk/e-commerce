package com.loopers.application.point;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointResult;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointFacade {

    private final UserService userService;
    private final PointService pointService;

    public PointOutput.Charge charge(PointInput.Charge input) {
        userService.getUser(input.userId());

        PointCommand.Charge command = input.toCommand();
        PointResult.Charge result = pointService.charge(command);
        return PointOutput.Charge.from(result);
    }

    public PointOutput.GetPoint getPoint(Long userId) {
        userService.getUser(userId);

        PointResult.GetPoint result = pointService.getPoint(userId);
        return PointOutput.GetPoint.from(result);
    }
}
