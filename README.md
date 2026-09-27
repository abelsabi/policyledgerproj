# HDFC Policy Ledger

Spring Boot 3 REST API for HDFC customers, policies, claims, and riders. Persistence is Spring Data JPA against Flyway-owned tables (H2 in `dev`, PostgreSQL in `prod`). Controllers return DTOs only.

## How to run

Java 17 and Maven are required.

```bash
mvn spring-boot:run
```

Default profile is `dev` (in-memory H2: `jdbc:h2:mem:hdfc;MODE=PostgreSQL;DB_CLOSE_DELAY=-1`). H2 console: `/h2-console`. Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

Production profile (placeholders only — set real values in the environment, never commit passwords):

```bash
set SPRING_PROFILES_ACTIVE=prod
set DB_URL=jdbc:postgresql://localhost:5432/hdfc
set DB_USER=hdfc
set DB_PASSWORD=changeme
mvn spring-boot:run
```

`spring.jpa.hibernate.ddl-auto` is `none`. Flyway applies `V1` schema and `V2` rider reference rows. Seed customers and policies are inserted through JPA in `DataSeeder` (skipped if `policyRepository.count() > 0`).

## Endpoints

| Method | Path | Status |
|--------|------|--------|
| GET | `/api/policies` | 200 — all policies, `policyNo` ascending |
| GET | `/api/policies/{policyNo}` | 200 / 404 |
| GET | `/api/policies?status=Active` | 200 — `findByStatusOrderByPolicyNoAsc` |
| GET | `/api/policies?type=TERM` | 200 — `findByProductTypeOrderByPolicyNoAsc` |
| GET | `/api/policies?customer=Anita%20Sharma` | 200 — `findByCustomer_FullNameOrderByPolicyNoAsc` |
| GET | `/api/policies/search?minPremium=20000` | 200 — JPQL `findWithPremiumAtLeast` |
| POST | `/api/policies` | 201 / 400 / 409 |
| DELETE | `/api/policies/{policyNo}` | 204 / 404 |
| GET | `/api/policies/{policyNo}/claims` | 200 / 404 |
| POST | `/api/claims` | 201 / 400 / 404 |

Validation errors return `400` with `status`, `error`, `message`, and a `fields` map. Unknown policies/claims return `404`. Duplicate `policyNo` returns `409`.

## Who owns `policy_riders`

`Policy` is the owning side of the `Policy` ↔ `Rider` many-to-many mapping. It declares `@JoinTable(name = "policy_riders")`. `Rider` uses `mappedBy = "riders"`.

## Why DTOs when `open-in-view` is false

With `spring.jpa.open-in-view=false`, the persistence context is closed when the service transaction ends, before the HTTP response is written. Controllers therefore map entities to DTOs inside `@Transactional` services, while associations are still loaded. Returning a `Policy` entity whose `riders` (or `claims`) collection is LAZY would leave Jackson to serialize after the session is gone. That typically throws `LazyInitializationException`, or worse, if someone switched collections to EAGER, it would dump the full entity graph (and possibly circular references) over HTTP. DTOs keep JSON stable, avoid session coupling, and never leak persistence proxies to the client.
