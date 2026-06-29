# Testing

The project uses JUnit 5, Mockito, Spring Boot Test, Spring Security Test, Spring Kafka Test, Testcontainers, and JaCoCo.

## Run All Tests And Coverage Checks

```powershell
.\order-service\mvnw.cmd -f pom.xml clean verify
```

The root Maven build runs all modules:

- `common`
- `order-service`
- `inventory-service`
- `payment-service`
- `notification-service`

## Run A Single Module

```powershell
.\order-service\mvnw.cmd -f pom.xml -pl order-service test
```

```powershell
.\order-service\mvnw.cmd -f pom.xml -pl inventory-service clean verify
```

## Coverage Gates

JaCoCo is configured in the root `pom.xml`:

| Counter | Minimum |
| --- | --- |
| Line coverage | `0.90` |
| Branch coverage | `0.80` |

Generated application classes and configuration properties classes are excluded from the coverage check.

Coverage reports are written to:

```text
<module>/target/site/jacoco/index.html
```

## Test Scope

The tests cover:

- Shared DTOs, events, enums, exceptions, and Kafka topics.
- Order service creation, lookup, paging, status transitions, and validation paths.
- Order controller and exception handling behavior.
- JWT token generation/validation and security filter behavior.
- Auth service demo credentials.
- AOP logging sanitization.
- Kafka event publisher/consumer behavior.
- Inventory, payment, and notification listener paths.
- Resilience4j fallback behavior through simulated external clients.

## Local Runtime Note

The project targets Java 21. Running the build on a newer JDK can produce noisy JaCoCo instrumentation warnings for newer JDK classes. The build should still pass when Maven exits with status `0`; using a Java 21 runtime avoids that noise.
