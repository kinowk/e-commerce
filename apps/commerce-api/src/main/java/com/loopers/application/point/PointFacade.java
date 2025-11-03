package com.loopers.application.point;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointResult;
import com.loopers.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointFacade {

    private final PointService pointService;

    public PointOutput.Charge charge(PointInput.Charge input) {
        PointCommand.Charge command = input.toCommand();
        PointResult.Charge result = pointService.charge(command);
        return PointOutput.Charge.from(result);
    }

    public PointOutput.GetPoint getPoint(Long userId) {
        PointResult.GetPoint result = pointService.getPoint(userId);
        return PointOutput.GetPoint.from(result);
    }
}
