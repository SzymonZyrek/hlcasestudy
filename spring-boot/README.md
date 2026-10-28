# Spring Boot 4 counterpart to the Jakarta EE case study

Same tiny **UserAccount** CRUD and business behavior as the repository's original Jakarta EE 10 + WildFly sample. Placed in a *subdirectory on the comparison branch* so that the original WAR is untouched and both variants are easy to inspect simultaneously.

**Current baseline (28 October 2026):** Java 21, Spring Boot 4.1.2, Spring MVC, Spring Data JPA, Hibernate, Jakarta Bean Validation, H2, Maven and JUnit/MockMvc.

## Run

From the repository root:

```bash
mvn -f spring-boot/pom.xml clean verify
mvn -f spring-boot/pom.xml spring-boot:run
```

The default port is **8081**, deliberately different from WildFly's usual 8080. No application server and no external database are required; H2 is in-memory and the initial users are seeded through Hibernate's `import.sql`.

```bash
curl http://localhost:8081/rest/useraccounts
curl 'http://localhost:8081/rest/useraccounts?pageSize=2&page=1'
curl http://localhost:8081/rest/useraccounts/1

curl -i -X POST http://localhost:8081/rest/useraccounts \
  -H 'Content-Type: application/json' \
  -d '{"username":"Lisa","age":8,"gender":"FEMALE"}'

curl -i -X PUT http://localhost:8081/rest/useraccounts \
  -H 'Content-Type: application/json' \
  -d '{"id":1,"age":21,"gender":"FEMALE"}'

curl -i -X DELETE http://localhost:8081/rest/useraccounts/2
```

## Comparing actual files

| Original Jakarta EE / WildFly | Spring Boot counterpart |
| --- | --- |
| `pom.xml` (WAR, provided Jakarta EE APIs) | `spring-boot/pom.xml` (executable JAR, starters, H2) |
| `config/JaxRsActivator.java` | `SpringCaseStudyApplication.java` + `@RestController` mappings |
| `rest/UserAccountEndpoint.java` (`@Path`, `@GET`, `@Inject`) | `rest/UserAccountController.java` (`@RequestMapping`, `@GetMapping`, constructor DI) |
| `dao/UserAccountRepository.java` (handwritten `EntityManager`) | `repository/UserAccountRepository.java` (Spring Data's `JpaRepository`) |
| JTA `@Transactional` on DAO mutations | Spring `@Transactional` on service methods |
| `model/UserAccount.java` with Jakarta JPA/Validation | Nearly identical Jakarta annotations in `model/UserAccount.java` |
| `META-INF/persistence.xml` and WildFly ExampleDS | `application.properties` and embedded H2 |
| `import.sql` (WildFly/DB-specific sequence) | `import.sql` (H2 sequence) |

The Spring sample adds **one service** to put transaction boundaries between web and repository layers, and **one record DTO** for the original partial-update request. This is intentionally shown, not hidden, because it affects framework-specific complexity.

## Matching behavior, with explicit differences

- Both expose list, optional 1-based page/pageSize pagination, GET by id, POST (201, empty body), PUT of age/gender with id **in JSON body** (204), and DELETE (204).
- Both keep the original `Gender` enum, Hibernate-generated ID, `@PrePersist` creation time, and username/age validation constraints.
- The Spring variant rejects invalid input at the HTTP boundary via `@Valid`, including malformed pagination (400). The original annotations exist on the entity, but the original JAX-RS endpoint **does not annotate request bodies with `@Valid`**, so do not claim identical HTTP validation.
- Jakarta EE original includes the deployed WAR context path `/hlcasestudy`; Spring uses `/rest/useraccounts` without that WAR prefix.
- The original XML has provider-level second-level/query cache flags. This minimal Spring demo does **not** configure a second-level cache. No performance comparison should be inferred from this sample.
- An H2 database and WildFly's ExampleDS are not the same persistence environment. This demo uses H2 solely so it runs without infrastructure.
- Both still use **JPA/Hibernate and Jakarta Validation**. Spring did not replace those standards; the biggest differences are the HTTP controller/DI and repository/transaction integration.
- Like other proxy-based Spring AOP transactions, `@Transactional` applies when calls cross a Spring-managed proxy. Avoid assuming a self-call (`this.otherMethod()`) creates a new transaction.

## Why no Spring Security, Lombok, MapStruct, Actuator, OpenAPI or Docker?

Because the original sample has none. Adding them would turn a framework comparison into an unfair comparison of two different applications. They can be explored separately later.

## Tests

`UserAccountIntegrationTest` loads the full Spring context and uses MockMvc against real MVC + Hibernate + H2. It covers CRUD, 1-based pagination, the seed data, `@PrePersist`, validation, and not-found behavior.

Run with `mvn -f spring-boot/pom.xml test`.

**Not a production template:** the in-memory database is destroyed on shutdown and Hibernate generates schema automatically. For production, use migrations, explicit access control, DTOs for all external requests, controlled error responses, and an actual database.
