package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec {

    private final OrderFacade orderFacade;

    @PostMapping
    @Override
    public ApiResponse<OrderResponse.Create> createOrder(
            @RequestHeader("X-USER-ID") String userLoginId,
            @RequestBody OrderRequest.Create request
    ) {
        return ApiResponse.success(OrderResponse.Create.from(orderFacade.createOrder(request.toInput(userLoginId))));
    }

    @GetMapping
    @Override
    public ApiResponse<List<OrderResponse.Summary>> getOrders(
            @RequestHeader("X-USER-ID") String userLoginId
    ) {
        return ApiResponse.success(
                orderFacade.getOrders(userLoginId).stream()
                        .map(OrderResponse.Summary::from)
                        .toList()
        );
    }

    @GetMapping("/{orderId}")
    @Override
    public ApiResponse<OrderResponse.Detail> getOrder(
            @RequestHeader("X-USER-ID") String userLoginId,
            @PathVariable Long orderId
    ) {
        return ApiResponse.success(OrderResponse.Detail.from(orderFacade.getOrder(userLoginId, orderId)));
    }
}
