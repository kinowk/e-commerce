package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.PgClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class PgClientImpl implements PgClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PgClientImpl(
            @Qualifier("pgRestTemplate") RestTemplate restTemplate,
            PgProperties pgProperties
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = pgProperties.baseUrl();
    }

    @Override
    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "requestPaymentFallback")
    @Retry(name = "pgRetry")
    public String requestPayment(Long orderId, String cardType, String cardNo, Long amount, String callbackUrl) {
        PgPaymentRequest request = new PgPaymentRequest(
                String.valueOf(orderId), cardType, cardNo, String.valueOf(amount), callbackUrl
        );

        PgPaymentResponse response = restTemplate.postForObject(
                baseUrl + "/api/v1/payments", request, PgPaymentResponse.class
        );

        if (response == null || response.transactionId() == null) {
            throw new IllegalStateException("PG 서버로부터 유효한 응답이 없습니다.");
        }

        return response.transactionId();
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

        ResponseEntity<List<PgTransactionDetail>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {}
        );

        List<PgTransactionDetail> body = response.getBody();
        if (body == null || body.isEmpty()) {
            return Optional.empty();
        }

        PgTransactionDetail latest = body.get(0);
        return Optional.of(new PgPaymentDetail(
                latest.transactionId(),
                latest.status(),
                latest.amount()
        ));
    }

    public Optional<PgPaymentDetail> getPaymentByOrderIdFallback(Long orderId, Throwable t) {
        log.warn("[PgClient] 결제 조회 실패 - orderId: {}, reason: {}", orderId, t.getMessage());
        return Optional.empty();
    }
}
