# Challenge: minimal Spring Boot 4 + Spring Data JPA

**Goal:** rewrite the same tiny UserAccount CRUD as short and idiomatic as possible, with **no new functionality or validation**.

Based on `comparison/spring-boot-4` (Spring Boot 4.1.2, Java 21, H2).

## Changes

- Drop the service layer. For this tiny example the controller calls Spring Data `JpaRepository` directly.
- Drop the update DTO: original `PUT` accepts the same `UserAccount` body as `POST`, with `id` in JSON and *only non-null age/gender* applied. No changes to username.
- Remove explicit HTTP `@Valid` and custom page-parameter validation that the original Jakarta EE sample did not have. **The existing JPA entity validation annotations remain unchanged.**
- Put `@Transactional` on the one partial-update method; Spring Data already handles transactions for `save` and `delete`. The managed JPA entity is updated by dirty checking.
- Retain the original response codes (201 POST, 204 PUT/DELETE, 404 unknown user), 1-based pagination, id ordering, `@PrePersist`, and Homer/Bart/Marge data.

The exercise is *not* a recommendation to omit a service layer for complex business logic. It measures the code needed for **this** application.

## Run

```bash
mvn clean verify
mvn spring-boot:run
```

Base URL: `http://localhost:8081/rest/useraccounts`

The integration test checks the unchanged CRUD contract, seed, 1-based pagination, missing pagination parameter behavior, timestamp and partial PUT. No validation was added to improve the test results.

## Compare

| Jakarta Data 1.0 | Spring Data JPA |
|---|---|
| `@Repository` + `CrudRepository<UserAccount, Long>` | `JpaRepository<UserAccount, Long>` |
| `@Find @OrderBy("id")` | `findAll(Sort.by("id"))` |
| `PageRequest.ofPage(page).size(size)` (1-based) | `PageRequest.of(page-1, size, sort)` (0-based) |
| JAX-RS `@Path/@GET/@POST` | Spring MVC `@RequestMapping/@GetMapping/@PostMapping` |
| CDI + JTA transactions | Spring DI + proxy-based transaction on PUT |

Both use standard Jakarta Persistence annotations in the entity. Spring's solution is not intended to be a smaller *runtime artifact* or a performance benchmark; compare application source and the resulting APIs.
