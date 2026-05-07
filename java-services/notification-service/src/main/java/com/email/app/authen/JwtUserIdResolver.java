package com.email.app.authen;

import com.commonservice.common.security.token.JwtTokenChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUserIdResolver {

    private final JwtTokenChecker jwtTokenChecker = new JwtTokenChecker();

    @Value("${app.notification.jwt.secret:${app.jwt.secret:}}")
    private String jwtSecret;

    public String resolveUserIdFromAuthorization(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("Missing Authorization header");
        }

        String prefix = "Bearer ";
        if (!authorizationHeader.startsWith(prefix)) {
            throw new IllegalArgumentException("Authorization header must be Bearer token");
        }

        String token = authorizationHeader.substring(prefix.length()).trim();
        if (token.isBlank() || jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalArgumentException("Invalid JWT configuration or token");
        }
        if (!jwtTokenChecker.isValid(token, jwtSecret)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        String userId = jwtTokenChecker.getUserId(token, jwtSecret);
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("Token does not contain user id");
        }
        return userId;
    }
}
