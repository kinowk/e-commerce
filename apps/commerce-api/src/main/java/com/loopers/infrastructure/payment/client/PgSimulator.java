package com.loopers.infrastructure.payment.client;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PgSimulator implements PaymentGateway {

    private final PgSimulatorClient client;
    private final ServerProperties serverProperties;

    @Override
    public void requestTransaction(Payment payment) {
        String hostName = serverProperties.getAddress().getHostName();
        Integer port = serverProperties.getPort();
        String callbackUrl = "http://" + hostName + ":" + port;

        PgSimulatorRequest.RequestTransaction requestTransaction = PgSimulatorRequest.RequestTransaction.of(payment, callbackUrl);
        ApiResponse<PgSimulatorResponse.RequestTransaction> response = client.requestTransaction(payment.getUserId(), requestTransaction);

        log.info("response: {}", response);
    }

}
