package com.loopers.interfaces.api.point;

import com.loopers.domain.user.attribute.Gender;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.interfaces.api.ApiHeader;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.UserRequest;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PointV1ApiE2ETest {

    private static final String ENDPOINT_GET_POINT = "/api/v1/points";
    private static final String ENDPOINT_CHARGE = "/api/v1/points/charge";
    private static final String ENDPOINT_JOIN_USER = "/api/v1/users";

    private final TestRestTemplate testRestTemplate;
    private final PointJpaRepository pointJpaRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public PointV1ApiE2ETest(TestRestTemplate testRestTemplate, PointJpaRepository pointJpaRepository, DatabaseCleanUp databaseCleanUp) {
        this.testRestTemplate = testRestTemplate;
        this.pointJpaRepository = pointJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    private void joinUser(String loginId) {
        UserRequest.Join request = new UserRequest.Join(
                "testuser", loginId, "password123", loginId + "@gmail.com", "1990-01-01", Gender.MALE
        );
        testRestTemplate.postForEntity(ENDPOINT_JOIN_USER, request, Void.class);
    }

    @DisplayName("GET /api/v1/points")
    @Nested
    class GetPoint {

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void returnsPoint_whenGetPointSucceeds() {
            // arrange
            joinUser("test123");

            HttpHeaders headers = new HttpHeaders();
            headers.set(ApiHeader.X_USER_ID, "test123");

            // act
            ParameterizedTypeReference<ApiResponse<PointResponse.GetPoint>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointResponse.GetPoint>> response = testRestTemplate.exchange(
                    ENDPOINT_GET_POINT, HttpMethod.GET,
                    new HttpEntity<>(headers), responseType
            );

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().loginId()).isEqualTo("test123"),
                    () -> assertThat(response.getBody().data().balance()).isEqualTo(0L)
            );
        }

        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void returns400_whenXUserIdHeaderIsMissing() {
            // act
            ParameterizedTypeReference<ApiResponse<PointResponse.GetPoint>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointResponse.GetPoint>> response = testRestTemplate.exchange(
                    ENDPOINT_GET_POINT, HttpMethod.GET,
                    new HttpEntity<>(null), responseType
            );

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/points/charge")
    @Nested
    class Charge {

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnsNewBalance_whenChargeSucceeds() {
            // arrange
            joinUser("test123");

            HttpHeaders headers = new HttpHeaders();
            headers.set(ApiHeader.X_USER_ID, "test123");
            PointRequest.Charge chargeRequest = new PointRequest.Charge(1000L);

            // act
            ParameterizedTypeReference<ApiResponse<PointResponse.Charge>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointResponse.Charge>> response = testRestTemplate.exchange(
                    ENDPOINT_CHARGE, HttpMethod.POST,
                    new HttpEntity<>(chargeRequest, headers), responseType
            );

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().loginId()).isEqualTo("test123"),
                    () -> assertThat(response.getBody().data().amount()).isEqualTo(1000L),
                    () -> assertThat(response.getBody().data().balance()).isEqualTo(1000L)
            );
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returns404_whenUserNotFound() {
            // arrange
            HttpHeaders headers = new HttpHeaders();
            headers.set(ApiHeader.X_USER_ID, "nonexist123");
            PointRequest.Charge chargeRequest = new PointRequest.Charge(1000L);

            // act
            ParameterizedTypeReference<ApiResponse<PointResponse.Charge>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointResponse.Charge>> response = testRestTemplate.exchange(
                    ENDPOINT_CHARGE, HttpMethod.POST,
                    new HttpEntity<>(chargeRequest, headers), responseType
            );

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
