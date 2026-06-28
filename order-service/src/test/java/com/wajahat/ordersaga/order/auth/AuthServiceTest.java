package com.wajahat.ordersaga.order.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.wajahat.ordersaga.common.exception.UnauthorizedException;
import com.wajahat.ordersaga.order.security.JwtProperties;
import com.wajahat.ordersaga.order.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtProperties jwtProperties;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(jwtTokenProvider, jwtProperties);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnResponse() {
        LoginRequest request = new LoginRequest("customer", "customer123");
        when(jwtProperties.expirationSeconds()).thenReturn(3600L);
        when(jwtTokenProvider.generateToken(eq("customer"), anyList())).thenReturn("test-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("test-token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresInSeconds());
    }

    @Test
    void login_WithInvalidUsername_ShouldThrowException() {
        LoginRequest request = new LoginRequest("invalid", "password");

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowException() {
        LoginRequest request = new LoginRequest("customer", "wrong-password");

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
    @Test
    void login_WithAdminCredentials_ShouldReturnResponse() {
        LoginRequest request = new LoginRequest("admin", "admin123");
        when(jwtProperties.expirationSeconds()).thenReturn(3600L);
        when(jwtTokenProvider.generateToken(eq("admin"), anyList())).thenReturn("admin-token");

        LoginResponse response = authService.login(request);

        assertEquals("admin-token", response.token());
    }

    @Test
    void login_WithServiceCredentials_ShouldReturnResponse() {
        LoginRequest request = new LoginRequest("service", "service123");
        when(jwtProperties.expirationSeconds()).thenReturn(3600L);
        when(jwtTokenProvider.generateToken(eq("service"), anyList())).thenReturn("service-token");

        LoginResponse response = authService.login(request);

        assertEquals("service-token", response.token());
    }
}
