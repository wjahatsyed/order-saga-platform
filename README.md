# Order SAGA Platform

Production-style Spring Boot assessment project for order processing using Java 21, Spring Boot 3.5, Kafka SAGA, JWT security, Redis caching, AOP logging, Resilience4j and OpenAPI documentation.

## Modules

- common
- order-service

Upcoming modules:

- inventory-service
- payment-service
- notification-service

## Current Status

Foundation branch contains base project structure, order-service bootstrap, common module, Docker infrastructure and shared dependency setup.

## Run

```bash
docker compose up -d
cd order-service
mvn spring-boot:run
```

## Demo Credentials

Use `POST /api/v1/auth/login` with one of these in-memory users:

| Username | Password | Role |
| --- | --- | --- |
| `customer` | `customer123` | `CUSTOMER` |
| `admin` | `admin123` | `ADMIN` |
| `service` | `service123` | `SERVICE` |
