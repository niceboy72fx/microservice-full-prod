package com.commonservice.common.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

public class JwtTokenChecker {
    public static final String CLAIM_USER_ID = "user_id";

    public Claims parseClaims(String token, String base64Secret) {
        return Jwts.parser()
                .verifyWith(getSigningKey(base64Secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token, String base64Secret) {
        try {
            Claims claims = parseClaims(token, base64Secret);
            return claims.getExpiration() != null && claims.getExpiration().getTime() > System.currentTimeMillis();
        } catch (Exception exception) {
            return false;
        }
    }

    public String getStringClaim(String token, String base64Secret, String claimKey) {
        Claims claims = parseClaims(token, base64Secret);
        return claims.get(claimKey, String.class);
    }

    public String getUserId(String token, String base64Secret) {
        Claims claims = parseClaims(token, base64Secret);
        String userId = claims.get(CLAIM_USER_ID, String.class);
        if (userId != null && !userId.isBlank()) {
            return userId;
        }
        return claims.getSubject();
    }

    private SecretKey getSigningKey(String base64Secret) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
