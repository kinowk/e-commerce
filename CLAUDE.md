# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Build
```bash
./gradlew build
./gradlew :apps:commerce-api:build
```

### Run (local profile requires Docker infra)
```bash
# Start required infrastructure (MySQL, Redis, Kafka)
docker-compose -f ./docker/infra-compose.yml up

# Run the API server
./gradlew :apps:commerce-api:bootRun
```

### Test
```bash
# All tests
./gradlew test

# Single module tests
./gradlew :apps:commerce-api:test

# Single test class
./gradlew :apps:commerce-api:test --tests "com.loopers.domain.example.ExampleServiceIntegrationTest"

# Single test method
./gradlew :apps:commerce-api:test --tests "com.loopers.domain.example.ExampleServiceIntegrationTest.Get.returnsExampleInfo_whenValidIdIsProvided"

# Code coverage report
./gradlew :apps:commerce-api:jacocoTestReport
```

## Architecture

### Multi-Module Layout

```
apps/        # Executable Spring Boot applications
modules/     # Reusable infrastructure configurations (jpa, redis, kafka)
supports/    # Add-on modules (jackson, logging, monitoring)
```

### commerce-api Layer Structure

The app follows hexagonal/clean architecture inside `com.loopers`:

| Package | Responsibility |
|---------|---------------|
| `interfaces/api/` | Controllers, DTOs, request/response objects, `@RestControllerAdvice` |
| `application/` | Facades that orchestrate domain services; `Input`/`Output` data objects |
| `domain/` | Business logic (`*Service`), domain models, domain repository interfaces, `*Command`/`*Result` objects |
| `infrastructure/` | JPA repository implementations, `*JpaRepository` (Spring Data) |

Data flow: Controller → Facade → Service → Repository impl → JPA

### Key Conventions

**Error handling**: Throw `CoreException(ErrorType, message)` from domain/application layers. `ApiControllerAdvice` maps these to `ApiResponse` with HTTP status. `ErrorType` enum defines: `BAD_REQUEST`, `NOT_FOUND`, `CONFLICT`, `INTERNAL_ERROR`.

**API response envelope**: All endpoints return `ApiResponse<T>` with `{ meta: { result, errorCode, message }, data }`.

**Test structure**: Three test tiers in `src/test/`:
- Unit tests (`*Test.java`): Plain JUnit, no Spring context, validate domain model constraints
- Integration tests (`*IntegrationTest.java`): `@SpringBootTest` with real DB via TestContainers, uses `DatabaseCleanUp` utility in `@AfterEach`
- E2E tests (`*E2ETest.java`): `@SpringBootTest(webEnvironment = RANDOM_PORT)` with `TestRestTemplate`, full HTTP stack

**Test style**: Use `@Nested` class per operation, `@DisplayName` on each test. Use arrange/act/assert comment blocks.

**User identity in requests**: `X-USER-ID` header (defined in `ApiHeader`) identifies the calling user.

### Infrastructure Modules

- **jpa**: Provides `JPAQueryFactory` bean, `BaseEntity`/`BaseTimeEntity` base classes, TestContainers MySQL fixture
- **redis**: Two `RedisTemplate` beans — default (reads from replica), master (reads from master)
- **kafka**: Batch listener config (3000 msg poll, manual commit, concurrency 3), TestContainers Kafka fixture
