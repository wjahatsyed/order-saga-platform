package com.wajahat.ordersaga.order.auth;

import com.wajahat.ordersaga.common.enums.Role;
import com.wajahat.ordersaga.common.exception.UnauthorizedException;
import com.wajahat.ordersaga.order.security.JwtProperties;
import com.wajahat.ordersaga.order.security.JwtTokenProvider;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final Map<String, DemoUser> users = Map.of(
            "customer", new DemoUser("customer123", Role.CUSTOMER),
            "admin", new DemoUser("admin123", Role.ADMIN),
            "service", new DemoUser("service123", Role.SERVICE)
    );

    public AuthService(JwtTokenProvider jwtTokenProvider, JwtProperties jwtProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    public LoginResponse login(LoginRequest request) {
        DemoUser user = users.get(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new UnauthorizedException("Invalid username or password");
        }
        String token = jwtTokenProvider.generateToken(request.username(), List.of(user.role()));
        return new LoginResponse(token, "Bearer", jwtProperties.expirationSeconds());
    }

    private record DemoUser(String password, Role role) {
    }
}
