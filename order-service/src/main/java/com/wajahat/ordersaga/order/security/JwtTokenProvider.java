package com.wajahat.ordersaga.order.security;

import com.wajahat.ordersaga.common.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private static final String ROLES_CLAIM = "roles";

    private final JwtProperties jwtProperties;
    private final SecretKey key;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, List<Role> roles) {
        Date issuedAt = new Date();
        Date expiresAt = Date.from(issuedAt.toInstant().plusSeconds(jwtProperties.expirationSeconds()));
        List<String> roleNames = roles.stream().map(Role::name).toList();

        return Jwts.builder()
                .subject(username)
                .claim(ROLES_CLAIM, roleNames)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            claims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String getUsername(String token) {
        return claims(token).getSubject();
    }

    public List<Role> getRoles(String token) {
        List<?> roles = claims(token).get(ROLES_CLAIM, List.class);
        return roles.stream()
                .map(Object::toString)
                .map(Role::valueOf)
                .toList();
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
