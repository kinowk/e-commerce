package com.loopers.infrastructure.order;

import com.loopers.domain.order.ExternalOrderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExternalOrderClientImpl implements ExternalOrderClient {

    @Override
    public void send(Long orderId) {
        log.info("[ExternalOrderClient] 주문 정보 외부 전송 (Mock) - orderId: {}", orderId);
    }
}
