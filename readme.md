# Jakarta EE / MicroProfile: strict 1:1 port

This branch is **not an extended version** of the case study. It ports the same WAR to an independent Jakarta EE + MicroProfile implementation, Open Liberty, to compare standardized enterprise-Java runtimes.

**Application Java source is byte-for-byte unchanged from `master`**:
- `JaxRsActivator`: same `/rest` prefix.
- `UserAccountEndpoint`: same HTTP paths, payloads, response codes and partial `PUT`.
- `UserAccountRepository`: same `EntityManager`, JPQL, JTA transactions, pagination and 404 handling.
- `UserAccount` and `Gender`: same entity, same existing validation annotations and `@PrePersist`.
- No `@Valid` added anywhere. No new manual validation, DTO, service layer, business logic, security or business endpoints.

## Runtime

- **Java 21**
- **Jakarta EE 11 Web Profile** (Jakarta REST, CDI, Persistence, Transactions, Bean Validation)
- **Eclipse MicroProfile 7.1** (Open Liberty runtime's supported platform; no new MicroProfile APIs are used by application code)
- **Open Liberty 26.0.0.10**, **EclipseLink** as the JPA provider
- H2 in-memory database, three original sample users; WAR

MicroProfile itself does **not** define persistence. The application still uses Jakarta Persistence and JTA. An honest 1:1 port does not need to invent usage of MP Config, Health, Fault Tolerance, etc. The MicroProfile runtime has platform-provided capabilities (potentially including administrative / standard endpoints), but **no additional application features** were introduced.

## Run

```bash
mvn clean package
mvn liberty:run
```

Or `mvn liberty:dev` for development and `mvn liberty:stop` from another terminal to stop the background server.

Same URL as WildFly:

```bash
curl http://localhost:9080/hlcasestudy/rest/useraccounts
curl 'http://localhost:9080/hlcasestudy/rest/useraccounts/?pageSize=2&page=1'
curl http://localhost:9080/hlcasestudy/rest/useraccounts/1

curl -i -X POST http://localhost:9080/hlcasestudy/rest/useraccounts \
  -H 'Content-Type: application/json' \
  -d '{"username":"Lisa","age":8,"gender":"FEMALE"}'

curl -i -X PUT http://localhost:9080/hlcasestudy/rest/useraccounts \
  -H 'Content-Type: application/json' \
  -d '{"id":"1","age":21,"gender":"FEMALE"}'

curl -i -X DELETE http://localhost:9080/hlcasestudy/rest/useraccounts/2
```

## Exact mapping against `master`

| Area | `master` | This branch |
| --- | --- | --- |
| Business Java | Jakarta EE | **Unchanged** |
| REST contract | `/hlcasestudy/rest/useraccounts` | **Unchanged** |
| JPA entities and validation constraints | `UserAccount` | **Unchanged** |
| SQL seed data | Homer / Bart / Marge | Same three users |
| Packaging | WAR | WAR |
| Runtime | WildFly | Open Liberty + MicroProfile 7.1 |
| Database | WildFly ExampleDS | Open Liberty H2 in-memory DataSource |
| Persistence provider | Hibernate | EclipseLink |
| Configuration | Hibernate-specific `persistence.xml`, older schema | Standard Jakarta Persistence 3.2, server-side JNDI DataSource |
| Port | 8080 | 9080 |

The provider-specific Hibernate second-level / query cache settings in the historical version are deliberately **not** replicated as new features of a different provider. Likewise, replacing the old vendor-specific seed SQL with equivalent H2 SQL is an infrastructure adjustment, not a change to the API.

The original repository does not add `@Valid` on REST method arguments. This variant intentionally does not add it either. Do not mistake identical code for a guarantee of byte-identical HTTP errors from different JSON providers; runtime-specific error representation may differ.

## Verification

GitHub Actions builds the WAR, boots Open Liberty and exercises the existing CRUD + pagination API using `scripts/smoke.py`. These tests do not add validation or behavior to the application.

The key point of this comparison: **MicroProfile complements Jakarta EE rather than replacing its JPA/JAX-RS API**. The application can move between standards-compliant runtimes with its business code unchanged.
