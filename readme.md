# Challenge: minimal Jakarta EE 11 + Jakarta Data 1.0

**Goal:** make the original UserAccount exercise as short and idiomatic as possible, without adding any behavior.

Based on `comparison/microprofile-open-liberty` (Open Liberty + MicroProfile 7.1 + Jakarta EE 11); the historical Jakarta EE implementation remains on `master`.

## Changes

- Handwritten `EntityManager` DAO replaced by the standardized **Jakarta Data 1.0** `CrudRepository<UserAccount, Long>`.
- One JAX-RS resource handles the same five HTTP operations. No service, DTO, AOP-style application features, new validation, custom exception mapper or error handler.
- The original JPA entity, validation annotations, JAX-RS activation, persistence unit, database and three demo records are unchanged.
- The unused `UserAccountException` is removed.
- Same HTTP endpoints, same 1-based pagination, ID-in-body **partial** PUT (only non-null age/gender), 201/204/404 statuses and seed data.
- No new `@Valid`: original Jakarta EE request bodies were not annotated with it. Existing JPA/Bean Validation constraints remain.

The platform still happens to provide MicroProfile capabilities, but application code uses only **Jakarta EE + Jakarta Data**, not any MicroProfile-specific API. MicroProfile is a runtime platform standard, not an alternative ORM.

## Run

```bash
mvn clean package
mvn liberty:run
```

Base URL: `http://localhost:9080/hlcasestudy/rest/useraccounts`

CI uses `mvn clean package`, provisions Open Liberty, starts the WAR, then runs the **same unchanged** black-box `scripts/smoke.py` used on the one-to-one MicroProfile branch.

## Compare

| Original `master` | This branch |
|---|---|
| Manual JPA queries and `EntityManager` | Jakarta Data `CrudRepository` + two `@Find` methods |
| JAX-RS endpoint delegates to DAO | JAX-RS endpoint injects repository directly |
| `@Transactional` on modifying DAO methods | Repository transactions; JTA boundary for partial update/delete |
| Explicit SQL ordering by id | `@OrderBy("id")` |
| Same JPA annotations and entity | Unchanged |

The goal is **minimal readable source**, not minimizing dependencies, shaving bytes off a JAR or providing a production architecture. This is educational; it is *not* a claim of identical behavior in every vendor-specific error case.
