# API Guide

The public REST API lives in `order-service` on port `8081`.

## Authentication

Login endpoint:

```http
POST /api/v1/auth/login
```

Request:

```json
{
  "username": "customer",
  "password": "customer123"
}
```

Response shape:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "<jwt>",
    "tokenType": "Bearer",
    "expiresInSeconds": 3600
  },
  "timestamp": "2026-06-28T00:00:00Z"
}
```

Demo users:

| Username | Password | Role |
| --- | --- | --- |
| `customer` | `customer123` | `CUSTOMER` |
| `admin` | `admin123` | `ADMIN` |
| `service` | `service123` | `SERVICE` |

Order endpoints require `CUSTOMER` or `ADMIN`.

## Orders

Create order:

```http
POST /api/v1/orders
```

Request:

```json
{
  "customerId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
  "items": [
    {
      "productId": "11111111-1111-1111-1111-111111111111",
      "quantity": 2,
      "unitPrice": 25.50
    }
  ]
}
```

Validation:

- `customerId` is required.
- `items` must not be empty.
- Each item requires `productId`.
- `quantity` must be positive.
- `unitPrice` must be at least `0.01`.

Successful creation returns HTTP `202 Accepted` with an `ApiResponse<OrderResponse>`.

Get order:

```http
GET /api/v1/orders/{orderId}
```

Get customer orders:

```http
GET /api/v1/orders/customers/{customerId}?page=0&size=10
```

## Swagger

- UI: `http://localhost:8081/swagger-ui.html`
- JSON: `http://localhost:8081/v3/api-docs`

## Error Responses

The global exception handler returns shared error DTOs for business exceptions, validation errors, and unexpected exceptions. Authentication failures return HTTP `401`.
