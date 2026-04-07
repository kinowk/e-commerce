---
name: Project Architecture & Recurring Issues
description: Hexagonal/clean arch Spring Boot e-commerce API; key patterns and recurring issues found in first code review (April 2026)
type: project
---

Spring Boot e-commerce API with strict hexagonal layering: interfaces/api -> application (facades) -> domain (services + models) -> infrastructure.

**Key patterns:**
- Records used for all DTOs, Commands, Results, Input/Output objects
- CoreException(ErrorType, message) is the single exception type across all layers
- Pessimistic locks via @Lock(PESSIMISTIC_WRITE) on JPA query methods named *ForUpdate
- Optimistic lock + @Retryable on Point.charge (version field)
- Redis cache (RedisTemplate<String,String> + ObjectMapper) for product detail and list; manual cache key construction
- ExternalOrderClient is a mock stub (just logs)

**Outbox / Kafka patterns (added April 2026):**
- Transactional Outbox pattern: OutboxEventListener saves to DB on BEFORE_COMMIT; OutboxRelayScheduler polls and sends via KafkaTemplate
- @Value fields used alongside @RequiredArgsConstructor — these fields are NOT final and are injected via field injection, not constructor injection; the fields must be declared without `final`
- OutboxRelayScheduler marks events published BEFORE the async Kafka send completes (race condition)
- Consumer self-invocation @Transactional anti-pattern: CatalogEventConsumer and OrderEventConsumer call protected processRecord() from within the same class — Spring proxy does not intercept this
- application.yml in commerce-streamer has duplicate top-level `spring:` keys — second block silently shadows the first in most parsers
- KafkaEventMessage uses Object payload — serialized immediately to JSON string in OutboxEvent, so the type unsafety is bounded but still fragile
- Duplicated consumer boilerplate: CatalogEventConsumer and OrderEventConsumer share identical consume() loop, parse+idempotency+log structure in processRecord(), differing only in event routing

**Recurring issues found in first review:**
- Excessive DTO pass-through layers: Request -> Input -> Command -> Result -> Output -> Response (identical fields copied 4-5 times for Order domain)
- Silent validation gap: Point.deduct throws BAD_REQUEST with no message; User validation methods throw BAD_REQUEST with no message — callers get unhelpful generic errors
- MAX_BALANCE in Point is `static` (not `static final`) — mutable class-level constant
- OrderService.createOrder does N individual saveItem() calls in a loop instead of batch save
- LikeService.addLike/removeLike: existsByUserIdAndProductId check + actual operation is not atomic; race condition window exists between the existence check and the save/delete
- ProductResult.List shadows java.util.List — confusing name
- ProductService.buildListCacheKey uses string concatenation — no delimiter between segments, potential key collisions (e.g., brandId=1,page=23 vs brandId=12,page=3)
- PointHistory is recorded on charge (amount=0 on signup) which writes a zero-amount history entry
- CouponService.useForOrder is @Transactional but is called from within OrderService's @Transactional — nested transaction; coupon lock acquired separately from the outer transaction's locks (correct but subtle)
- ProductSortType has a `default` branch in switch that duplicates the null-check fallthrough — could use exhaustive switch
- UserV1Controller.getCurrentUser receives X-USER-ID as String loginId but other controllers receive it as Long userId — inconsistent header semantics
- No @Valid on UserRequest.Join or OrderRequest.Create — validation delegated entirely to domain constructor, bypassing Spring MVC validation infrastructure
