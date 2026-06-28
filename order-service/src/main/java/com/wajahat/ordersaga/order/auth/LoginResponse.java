package com.wajahat.ordersaga.order.auth;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds
) {
}
