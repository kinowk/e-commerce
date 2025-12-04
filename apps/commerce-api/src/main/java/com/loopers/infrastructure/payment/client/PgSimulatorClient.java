package com.loopers.infrastructure.payment.client;

import com.loopers.interfaces.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pg-simulator-client", url = "${external.pg-simulator.base-url}")
public interface PgSimulatorClient {

    String X_USER_ID = "X-USER-ID";

    @PostMapping("/api/v1/payments")
    ApiResponse<PgSimulatorResponse.RequestTransaction> requestTransaction(
            @RequestHeader(X_USER_ID) Long userId,
            @RequestBody PgSimulatorRequest.RequestTransaction request
    );

    @GetMapping("/api/v1/payments/{transactionKey}")
    ApiResponse<PgSimulatorResponse.GetTransaction> getTransaction(
            @RequestHeader(X_USER_ID) Long userId,
            @PathVariable String transactionKey
    );

    @GetMapping("/api/v1/payments")
    ApiResponse<PgSimulatorResponse.GetTransactions> getTransactions(
            @RequestHeader(X_USER_ID) Long userId,
            @RequestParam String orderUid
    );
}
