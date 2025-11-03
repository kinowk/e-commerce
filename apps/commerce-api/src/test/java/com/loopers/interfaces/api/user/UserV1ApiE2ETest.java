package com.loopers.interfaces.api.user;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.infrastructure.user.UserJpaRepository;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ApiE2ETest {

    private static final String ENDPOINT_JOIN_USER = "/api/v1/users";
    private static final Function<String, String> ENDPOINT_GET_USER = loginId -> "/api/v1/users/" + loginId;

    private final TestRestTemplate testRestTemplate;
    private final UserJpaRepository userJpaRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public UserV1ApiE2ETest(
            TestRestTemplate testRestTemplate,
            UserJpaRepository userJpaRepository,
            DatabaseCleanUp databaseCleanUp
    ) {
        this.testRestTemplate = testRestTemplate;
        this.userJpaRepository = userJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원가입 시")
    @Nested
    class Join {

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUser_whenJoinSuccess() {
            //given
            String loginId = "testuser";
            Gender gender = Gender.MALE;
            String birthDate = "2000-01-01";
            String email = "test@gmail.com";
            UserRequest.Join request = new UserRequest.Join(loginId, gender, birthDate, email);
            HttpEntity<UserRequest.Join> httpEntity = new HttpEntity<>(request);

            //when
            ResponseEntity<ApiResponse<UserResponse.Join>> response = testRestTemplate.exchange(
                    ENDPOINT_JOIN_USER,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            ApiResponse<UserResponse.Join> body = response.getBody();
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(body).isNotNull(),
                    () -> assertThat(body.data().loginId()).isEqualTo(loginId),
                    () -> assertThat(body.data().gender()).isEqualTo(gender),
                    () -> assertThat(body.data().birthDate()).isEqualTo(birthDate),
                    () -> assertThat(body.data().email()).isEqualTo(email)
            );

            User savedUser = userJpaRepository.findAll().get(0);
            assertThat(savedUser.getLoginId()).isEqualTo(loginId);
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.")
        @Test
        void throwsException_whenGenderIsNull() {
            //given
            UserRequest.Join request = new UserRequest.Join("testuser", null, "2020-01-01", "test@gmail.com");
            HttpEntity<UserRequest.Join> httpEntity = new HttpEntity<>(request);

            //when
            ResponseEntity<ApiResponse<UserResponse.Join>> response = testRestTemplate.exchange(
                    ENDPOINT_JOIN_USER,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("회원조회 시")
    @Nested
    class Search {

        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUser_whenFindUserSuccess() {
            //given
            String loginId = "testuser";
            Gender gender = Gender.MALE;
            String birthDate = "2020-01-01";
            String email = "test@gmail.com";
            User user = User.builder()
                    .loginId(loginId)
                    .gender(gender)
                    .birthDate(birthDate)
                    .email(email)
                    .build();

            userJpaRepository.save(user);

            String requestUrl = ENDPOINT_GET_USER.apply(loginId);

            //when
            ResponseEntity<ApiResponse<UserResponse.GetUser>> response = testRestTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(null),
                    new ParameterizedTypeReference<>() {
                    }
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            ApiResponse<UserResponse.GetUser> body = response.getBody();
            assertAll(
                    () -> assertThat(body.data().loginId()).isEqualTo(loginId),
                    () -> assertThat(body.data().gender()).isEqualTo(gender),
                    () -> assertThat(body.data().birthDate()).isEqualTo(birthDate),
                    () -> assertThat(body.data().email()).isEqualTo(email)
            );

        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void throwsNotFoundException_whenInvalidLoginId() {
            //given
            String loginId = "testuser";
            String requestUrl = ENDPOINT_GET_USER.apply(loginId);

            //when
            ResponseEntity<ApiResponse<UserResponse.GetUser>> response = testRestTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(null),
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

    }
}

