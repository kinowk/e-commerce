package com.loopers.interfaces.api.payment;

import com.loopers.domain.payment.PaymentService;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller {

    private final PaymentService paymentService;

    @PostMapping("/callback")
    public ApiResponse<Void> callback(@RequestBody PaymentRequest.Callback request) {
        paymentService.handleCallback(request.toCommand());
        return ApiResponse.success(null);
    }

    @PostMapping("/sync")
    public ApiResponse<PaymentResponse.Detail> sync(@RequestParam Long orderId) {
        return ApiResponse.success(
                PaymentResponse.Detail.from(paymentService.syncPayment(orderId))
        );
    }

    @GetMapping
    public ApiResponse<PaymentResponse.Detail> getPayment(@RequestParam Long orderId) {
        return ApiResponse.success(
                PaymentResponse.Detail.from(paymentService.getPaymentByOrderId(orderId))
        );
    }
}
