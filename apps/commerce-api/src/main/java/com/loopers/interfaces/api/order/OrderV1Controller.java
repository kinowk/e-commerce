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
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody OrderRequest.Create request
    ) {
        return ApiResponse.success(OrderResponse.Create.from(orderFacade.createOrder(request.toInput(userId))));
    }

    @GetMapping
    @Override
    public ApiResponse<List<OrderResponse.Summary>> getOrders(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return ApiResponse.success(
                orderFacade.getOrders(userId).stream()
                        .map(OrderResponse.Summary::from)
                        .toList()
        );
    }

    @GetMapping("/{orderId}")
    @Override
    public ApiResponse<OrderResponse.Detail> getOrder(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long orderId
    ) {
        return ApiResponse.success(OrderResponse.Detail.from(orderFacade.getOrder(userId, orderId)));
    }
}
