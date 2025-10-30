package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.application.point.PointInput;
import com.loopers.application.point.PointOutput;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {

    private final PointFacade pointFacade;

    @GetMapping("/{userId}")
    @Override
    public ApiResponse<PointResponse.GetPoint> getPoint(@PathVariable Long userId) {
        PointOutput.GetPoint output = pointFacade.getPoint(userId);
        PointResponse.GetPoint response = PointResponse.GetPoint.from(output);
        return ApiResponse.success(response);
    }

    @PostMapping("/charge")
    @Override
    public ApiResponse<PointResponse.Charge> charge(@RequestBody PointRequest.Charge request) {
        PointInput.Charge input = request.toInput();
        PointOutput.Charge output = pointFacade.charge(input);
        PointResponse.Charge response = PointResponse.Charge.from(output);
        return ApiResponse.success(response);
    }

}
