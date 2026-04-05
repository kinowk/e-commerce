package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.PgClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class PgClientImpl implements PgClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PgClientImpl(
            @Qualifier("pgRestTemplate") RestTemplate restTemplate,
            @Value("${pg.base-url}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "requestPaymentFallback")
    @Retry(name = "pgRetry")
    public String requestPayment(Long orderId, String cardType, String cardNo, Long amount, String callbackUrl) {
        Map<String, Object> request = Map.of(
                "orderId", String.valueOf(orderId),
                "cardType", cardType,
                "cardNo", cardNo,
                "amount", String.valueOf(amount),
                "callbackUrl", callbackUrl
        );

        Map<?, ?> response = restTemplate.postForObject(
                baseUrl + "/api/v1/payments", request, Map.class
        );

        if (response == null) {
            throw new IllegalStateException("PG 서버로부터 응답이 없습니다.");
        }

        return (String) response.get("transactionId");
    }

    public String requestPaymentFallback(Long orderId, String cardType, String cardNo, Long amount, String callbackUrl, Throwable t) {
        log.warn("[PgClient] 결제 요청 실패 - orderId: {}, reason: {}", orderId, t.getMessage());
        return null;
    }

    @Override
    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "getPaymentByOrderIdFallback")
    public Optional<PgPaymentDetail> getPaymentByOrderId(Long orderId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/v1/payments")
                .queryParam("orderId", orderId)
                .toUriString();

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> body = response.getBody();
        if (body == null || body.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> latest = body.get(0);
        return Optional.of(new PgPaymentDetail(
                (String) latest.get("transactionId"),
                (String) latest.get("status"),
                toLong(latest.get("amount"))
        ));
    }

    public Optional<PgPaymentDetail> getPaymentByOrderIdFallback(Long orderId, Throwable t) {
        log.warn("[PgClient] 결제 조회 실패 - orderId: {}, reason: {}", orderId, t.getMessage());
        return Optional.empty();
    }

    private Long toLong(Object value) {
        if (value instanceof Number n) {
            return n.longValue();
        }
        return null;
    }
}
