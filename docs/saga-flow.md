# SAGA Flow

The SAGA is event-driven through Kafka. `order-service` starts the flow by saving a pending order and publishing `order.created`.

## Happy Path

```mermaid
sequenceDiagram
    participant O as order-service
    participant K as Kafka
    participant I as inventory-service
    participant P as payment-service
    participant N as notification-service

    O->>O: Save order as PENDING
    O->>K: order.created
    K->>I: order.created
    I->>I: Check availability and decrement stock
    I->>K: inventory.reserved
    K->>P: inventory.reserved
    P->>P: Charge simulated payment gateway
    P->>K: payment.completed
    K->>O: payment.completed
    O->>O: Mark order CONFIRMED
    O->>K: order.confirmed
    K->>N: order.confirmed
```

## Inventory Rejection

```mermaid
sequenceDiagram
    participant O as order-service
    participant K as Kafka
    participant I as inventory-service
    participant N as notification-service

    O->>K: order.created
    K->>I: order.created
    I->>I: Availability or stock check fails
    I->>K: inventory.rejected
    K->>O: inventory.rejected
    O->>O: Mark order CANCELLED
    O->>K: order.cancelled
    K->>N: order.cancelled
```

## Payment Failure

```mermaid
sequenceDiagram
    participant O as order-service
    participant K as Kafka
    participant I as inventory-service
    participant P as payment-service
    participant N as notification-service

    O->>K: order.created
    K->>I: order.created
    I->>K: inventory.reserved
    K->>P: inventory.reserved
    P->>P: Payment gateway simulation fails
    P->>K: payment.failed
    K->>O: payment.failed
    O->>O: Mark order CANCELLED
    O->>K: order.cancelled
    K->>N: order.cancelled
```

## Current Limitations

- `inventory.released` and `payment.refunded` exist as shared topic constants and event records, but compensation consumers/publishers are not wired yet.
- Payment uses a fixed dummy amount in the payment listener.
- Event handling is intentionally concise for assessment readability; production-grade idempotency, outbox, and dead-letter handling should be added before real deployment.
