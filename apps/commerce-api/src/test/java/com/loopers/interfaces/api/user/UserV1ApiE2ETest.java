package com.loopers.interfaces.api.user;

import com.loopers.domain.user.attribute.Gender;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.ApiHeader;
import com.loopers.interfaces.api.ApiResponse;
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

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserV1ApiE2ETest {

    private static final String ENDPOINT_JOIN_USER = "/api/v1/users";
    private static final Function<String, String> ENDPOINT_GET_USER = loginId -> "/api/v1/users/" + loginId;
    private static final String ENDPOINT_GET_CURRENT_USER = "/api/v1/users/me";

    private final TestRestTemplate testRestTemplate;
    private final UserJpaRepository userJpaRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public UserV1ApiE2ETest(TestRestTemplate testRestTemplate, UserJpaRepository userJpaRepository, DatabaseCleanUp databaseCleanUp) {
        this.testRestTemplate = testRestTemplate;
        this.userJpaRepository = userJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/users")
    @Nested
    class Join {

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInfo_whenJoinSucceeds() {
            // arrange
            UserRequest.Join request = new UserRequest.Join(
                    "testuser", "test123", "password123", "test@gmail.com", "1990-01-01", Gender.MALE
            );

            // act
            ParameterizedTypeReference<ApiResponse<UserResponse.Join>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserResponse.Join>> response = testRestTemplate.exchange(
                    ENDPOINT_JOIN_USER, HttpMethod.POST,
                    new HttpEntity<>(request), responseType
            );

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().id()).isNotNull(),
                    () -> assertThat(response.getBody().data().loginId()).isEqualTo("test123"),
                    () -> assertThat(response.getBody().data().username()).isEqualTo("testuser"),
                    () -> assertThat(response.getBody().data().email()).isEqualTo("test@gmail.com"),
                    () -> assertThat(response.getBody().data().birthDate()).isEqualTo("1990-01-01"),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(Gender.MALE)
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void returns400_whenGenderIsMissing() {
            // arrange
            UserRequest.Join request = new UserRequest.Join(
                    "testuser", "test123", "password123", "test@gmail.com", "1990-01-01", null
            );

            // act
            ParameterizedTypeReference<ApiResponse<UserResponse.Join>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserResponse.Join>> response = testRestTemplate.exchange(
                    ENDPOINT_JOIN_USER, HttpMethod.POST,
                    new HttpEntity<>(request), responseType
            );

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    class GetCurrentUser {

        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInfo_whenGetCurrentUserSucceeds() {
            // arrange
            UserRequest.Join joinRequest = new UserRequest.Join(
                    "testuser", "test123", "password123", "test@gmail.com", "1990-01-01", Gender.MALE
            );
            ParameterizedTypeReference<ApiResponse<UserResponse.Join>> joinResponseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserResponse.Join>> joinResponse = testRestTemplate.exchange(
                    ENDPOINT_JOIN_USER, HttpMethod.POST, new HttpEntity<>(joinRequest), joinResponseType
            );
            Long userId = joinResponse.getBody().data().id();

            HttpHeaders headers = new HttpHeaders();
            headers.set(ApiHeader.X_USER_ID, String.valueOf(userId));

            // act
            ParameterizedTypeReference<ApiResponse<UserResponse.GetUser>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserResponse.GetUser>> response = testRestTemplate.exchange(
                    ENDPOINT_GET_CURRENT_USER, HttpMethod.GET,
                    new HttpEntity<>(headers), responseType
            );

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().loginId()).isEqualTo("test123"),
                    () -> assertThat(response.getBody().data().username()).isEqualTo("testuser"),
                    () -> assertThat(response.getBody().data().email()).isEqualTo("test@gmail.com"),
                    () -> assertThat(response.getBody().data().id()).isNotNull()
            );
        }

        @DisplayName("존재하지 않는 ID로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void returns404_whenUserNotFound() {
            // arrange
            HttpHeaders headers = new HttpHeaders();
            headers.set(ApiHeader.X_USER_ID, "999999");

            // act
            ParameterizedTypeReference<ApiResponse<UserResponse.GetUser>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserResponse.GetUser>> response = testRestTemplate.exchange(
                    ENDPOINT_GET_CURRENT_USER, HttpMethod.GET,
                    new HttpEntity<>(headers), responseType
            );

            // assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
