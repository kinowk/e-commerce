package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderInput;
import com.loopers.application.order.OrderOutput;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec {

    private final OrderFacade orderFacade;

    @GetMapping
    @Override
    public ApiResponse<OrderResponse.GetOrderSummary> getOrderSummary(@RequestHeader("X-USER-ID") Long userId) {
        return null;
    }

    @GetMapping("/{orderId}")
    @Override
    public ApiResponse<OrderResponse.GetOrderDetail> getOrderDetail(@RequestHeader("X-USER-ID") Long userId, @PathVariable Long orderId) {
        OrderInput.GetOrderDetail input = new OrderInput.GetOrderDetail(userId, orderId);
        OrderOutput.GetOrderDetail output = orderFacade.getOrderDetail(input);
        OrderResponse.GetOrderDetail response = OrderResponse.GetOrderDetail.from(output);
        return ApiResponse.success(response);
    }

    @PostMapping
    @Override
    public ApiResponse<OrderResponse.Create> create(@RequestHeader("X-USER-ID") Long userId, @RequestBody OrderRequest.Create request) {
        OrderInput.Create input = new OrderInput.Create(
                userId,
                request.products().stream()
                        .map(product -> new OrderInput.Create.Product(
                                product.productOptionId(),
                                product.quantity()
                        ))
                        .toList()
        );

        OrderOutput.Create output = orderFacade.create(input);
        OrderResponse.Create response = OrderResponse.Create.from(output);

        return ApiResponse.success(response);
    }
}
