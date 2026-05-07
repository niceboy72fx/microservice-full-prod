package com.authentication.app.authen;

import com.commonservice.common.security.token.JwtTokenChecker;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    private final JwtProperties jwtProperties;
    private final JwtTokenChecker jwtTokenChecker = new JwtTokenChecker();

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(String username) {
        return generateToken(username, jwtProperties.getExpirationSeconds(), ACCESS_TOKEN);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, jwtProperties.getRefreshExpirationSeconds(), REFRESH_TOKEN);
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractUserId(String token) {
        return jwtTokenChecker.getUserId(token, jwtProperties.getSecret());
    }

    public boolean isTokenValid(String token) {
        return jwtTokenChecker.isValid(token, jwtProperties.getSecret());
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return REFRESH_TOKEN.equals(claims.get(CLAIM_TOKEN_TYPE, String.class));
        } catch (Exception ex) {
            return false;
        }
    }

    public Duration remainingTtl(String token) {
        Claims claims = extractClaims(token);
        Instant expiration = claims.getExpiration().toInstant();
        Instant now = Instant.now();
        if (expiration.isBefore(now)) {
            return Duration.ZERO;
        }
        return Duration.between(now, expiration);
    }

    private String generateToken(String username, long expirySeconds, String tokenType) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirySeconds);
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .claim(JwtTokenChecker.CLAIM_USER_ID, username)
                .claim(CLAIM_TOKEN_TYPE, tokenType)
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractClaims(String token) {
        return jwtTokenChecker.parseClaims(token, jwtProperties.getSecret());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
