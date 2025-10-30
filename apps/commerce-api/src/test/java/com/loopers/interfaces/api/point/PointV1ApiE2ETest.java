package com.loopers.interfaces.api.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.infrastructure.point.PointJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointV1ApiE2ETest {

    private static final String SAVED_USER = "testuser";
    private static final Long INITIAL_BALANCE = 0L;
    private static final String X_USER_ID = "X-USER-ID";
    private static final String ENDPOINT_CHARGE_POINT = "/api/v1/points/charge";
    private static final String ENDPOINT_GET_POINT = "/api/v1/points/users";

    private final TestRestTemplate testRestTemplate;
    private final UserJpaRepository userJpaRepository;
    private final PointJpaRepository pointJpaRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public PointV1ApiE2ETest(
            TestRestTemplate testRestTemplate,
            UserJpaRepository userJpaRepository,
            PointJpaRepository pointJpaRepository,
            DatabaseCleanUp databaseCleanUp
    ) {
        this.testRestTemplate = testRestTemplate;
        this.userJpaRepository = userJpaRepository;
        this.pointJpaRepository = pointJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @DisplayName("회원 저장")
    @BeforeEach
    void saveUser() {
        User user = User.builder()
                .loginId(SAVED_USER)
                .gender(Gender.MALE)
                .birthDate("2000-01-01")
                .email("test@gmail.com")
                .build();

        User savedUser = userJpaRepository.save(user);

        Point point = Point.builder()
                .userId(savedUser.getId())
                .balance(INITIAL_BALANCE)
                .build();

        pointJpaRepository.save(point);
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트 충전 시")
    @Nested
    class Charge {

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnsBalance_whenExistUserCharge1000Points() {
            //given
            Long amount = 1000L;
            User savedUser = userJpaRepository.findByLoginId(SAVED_USER).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));

            PointRequest.Charge request = new PointRequest.Charge(savedUser.getId(), amount);
            HttpEntity<PointRequest.Charge> httpEntity = new HttpEntity<>(request);

            //when
            ResponseEntity<ApiResponse<PointResponse.Charge>> response = testRestTemplate.exchange(
                    ENDPOINT_CHARGE_POINT,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            ApiResponse<PointResponse.Charge> body = response.getBody();

            assertThat(body.data().balance()).isEqualTo(INITIAL_BALANCE + amount);
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void throwsException_whenInvalidUser() {
            //given
            Long userId = -1L;

            HttpHeaders headers = new HttpHeaders();
            headers.set(X_USER_ID, String.valueOf(userId));
            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
            //when
            ResponseEntity<ApiResponse<PointResponse.GetPoint>> response = testRestTemplate.exchange(
                    ENDPOINT_GET_POINT,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @DisplayName("포인트 조회 시")
    @Nested
    class Search {

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void returnsPoint_whenGetPointSuccess() {
            //given
            User savedUser = userJpaRepository.findByLoginId(SAVED_USER).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
            Long userId = savedUser.getId();

            HttpHeaders headers = new HttpHeaders();
            headers.set(X_USER_ID, String.valueOf(userId));
            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

            //when
            ResponseEntity<ApiResponse<PointResponse.GetPoint>> response = testRestTemplate.exchange(
                    ENDPOINT_GET_POINT,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            ApiResponse<PointResponse.GetPoint> body = response.getBody();

            assertThat(body.data().balance()).isEqualTo(INITIAL_BALANCE);
        }

        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void throwsException_whenHeaderNotContains() {
            //when
            ResponseEntity<ApiResponse<PointResponse.GetPoint>> response = testRestTemplate.exchange(
                    ENDPOINT_GET_POINT,
                    HttpMethod.GET,
                    new HttpEntity<>(null),
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
