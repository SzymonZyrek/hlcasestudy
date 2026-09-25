# Jakarta EE REST Case Study

> **Small enterprise-Java case study**
>
> A compact Jakarta EE application implementing validated CRUD and pagination for user accounts.

The repository is intentionally small. It is useful as a public sample of straightforward JAX-RS/JPA mechanics rather than a production application.

## Stack

- Java 11
- Jakarta EE 10 API
- JAX-RS
- CDI
- JPA / `EntityManager`
- Bean Validation
- WildFly
- Maven / WAR packaging

## Structure

```text
REST endpoint
    ↓
UserAccountRepository
    ↓
JPA EntityManager
    ↓
UserAccount entity
```

The REST endpoint supports:

- list all users;
- paginated listing with `pageSize` and `page`;
- fetch by ID;
- create;
- partial update of mutable fields;
- delete.

The entity includes validation for username, age and gender and records creation time on persistence.

## Build

```bash
mvn clean install
```

## Deploy to WildFly

```bash
cp -rf target/hlcasestudy.war $WILDFLY_HOME/standalone/deployments
```

## Example requests

```bash
curl http://localhost:8080/hlcasestudy/rest/useraccounts

curl 'http://localhost:8080/hlcasestudy/rest/useraccounts/?pageSize=2&page=1'

curl -X POST http://localhost:8080/hlcasestudy/rest/useraccounts \
  -H 'Content-Type: application/json' \
  -d '{"username":"szymon","age":36,"gender":"MALE"}'

curl -X PUT http://localhost:8080/hlcasestudy/rest/useraccounts \
  -H 'Content-Type: application/json' \
  -d '{"id":"1","age":21,"gender":"FEMALE"}'

curl -X DELETE http://localhost:8080/hlcasestudy/rest/useraccounts/2
```

## Status

Small historical/case-study repository. It demonstrates ordinary enterprise-Java plumbing and is not intended as a full application architecture.
