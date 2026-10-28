# Spring Boot 4 — UserAccount REST Case Study

This branch ports the original [Jakarta EE / WildFly version on `master`](https://github.com/SzymonZyrek/hlcasestudy/tree/master) to Spring Boot **4.1.2** and Java **21**. It is an alternative implementation of the *same* small CRUD exercise, not an additional application in a subdirectory.

The usual Maven project layout applies: `pom.xml`, `src/main/java`, `src/main/resources`, and `src/test/java`. The original Jakarta sources remain on `master` and are **not duplicated** on this branch.

## Stack

- Spring Boot 4.1.2, Spring MVC and constructor injection
- Spring Data JPA / Hibernate / Jakarta Validation
- Embedded H2 (in-memory) and Maven
- JUnit and MockMvc integration tests
- Java 21

## Build and run

From the repository root:

```bash
mvn clean verify
mvn spring-boot:run
```

The Spring Boot server listens on port **8081**. H2 is in-memory and initialized with the original example users.

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

## Comparing the branches

| Jakarta EE (`master`) | Spring Boot (`comparison/spring-boot-4`) |
| --- | --- |
| WAR, provided Jakarta EE APIs, WildFly | Spring Boot executable JAR and embedded server |
| `config/JaxRsActivator.java` | `SpringCaseStudyApplication.java` |
| `rest/UserAccountEndpoint.java` (JAX-RS) | `rest/UserAccountController.java` (Spring MVC) |
| `dao/UserAccountRepository.java` (manual `EntityManager`) | `repository/UserAccountRepository.java` (`JpaRepository`) |
| JTA `@Transactional` on DAO mutations | Spring `@Transactional` on service methods |
| Jakarta JPA entity and validation | Jakarta JPA entity and validation |
| `META-INF/persistence.xml`, WildFly ExampleDS | `application.properties`, embedded H2 |

The Spring port adds a service layer for transaction boundaries and a record DTO for partial PUT. Both implementations support CRUD and 1-based pagination. Spring explicitly validates HTTP request bodies using `@Valid` and returns 400 for malformed pagination; the original JAX-RS endpoint does not use `@Valid` on request bodies. Spring's route has no WAR context prefix.

The historical persistence configuration includes provider-level caching flags; this Spring example does not configure second-level caching. It intentionally excludes unrelated production features such as security, telemetry, and database migrations so the frameworks can be compared on a small, consistent task.

## Tests

`mvn test` runs a full Spring context with MockMvc, Hibernate, and H2; CRUD, seed, pagination, validation, timestamps, and not-found behavior are covered.

**Scope:** educational framework comparison, not a production template. Do not infer runtime-performance conclusions from these configurations.
