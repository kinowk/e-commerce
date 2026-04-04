package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.application.point.PointInput;
import com.loopers.application.point.PointOutput;
import com.loopers.interfaces.api.ApiHeader;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {

    private final PointFacade pointFacade;

    @GetMapping
    @Override
    public ApiResponse<PointResponse.GetPoint> getPoint(@RequestHeader(ApiHeader.X_USER_ID) String loginId) {
        PointOutput.GetPoint output =  pointFacade.getPoint(loginId);
        PointResponse.GetPoint response = PointResponse.GetPoint.from(output);
        return ApiResponse.success(response);
    }

    @PostMapping("/charge")
    @Override
    public ApiResponse<PointResponse.Charge> charge(
            @RequestHeader(ApiHeader.X_USER_ID) String loginId,
            @Valid @RequestBody PointRequest.Charge request
    ) {
        PointInput.Charge input = new PointInput.Charge(loginId, request.amount());
        PointOutput.Charge output = pointFacade.charge(input);
        PointResponse.Charge response = PointResponse.Charge.from(output);
        return ApiResponse.success(response);
    }

}
