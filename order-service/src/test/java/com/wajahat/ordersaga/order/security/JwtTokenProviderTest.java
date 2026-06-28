package com.wajahat.ordersaga.order.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wajahat.ordersaga.common.enums.Role;
import java.util.List;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {
    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            new JwtProperties("order-saga-demo-secret-key-for-jwt-signing-2026", 3600)
    );

    @Test
    void generatesAndParsesToken() {
        String token = jwtTokenProvider.generateToken("customer", List.of(Role.CUSTOMER));

        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("customer", jwtTokenProvider.getUsername(token));
        assertEquals(List.of(Role.CUSTOMER), jwtTokenProvider.getRoles(token));
    }

    @Test
    void rejectsInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("not-a-token"));
    }
}
