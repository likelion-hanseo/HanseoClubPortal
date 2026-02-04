// src/main/java/hello/hscp/global/security/JwtTokenProvider.java
package hello.hscp.global.security;

import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;

    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-ttl-seconds:3600}") long accessTtlSeconds,
            @Value("${app.jwt.refresh-ttl-seconds:2592000}") long refreshTtlSeconds
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("app.jwt.secret must be >= 32 chars");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    public String createAccessToken(Long userId, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTtlSeconds);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role.name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshTtlSeconds);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("typ", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserId(String token) {
        Claims c = parseClaims(token);
        try {
            return Long.valueOf(c.getSubject());
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INVALID_TOKEN, "Invalid subject");
        }
    }

    public Role getRole(String token) {
        Claims c = parseClaims(token);
        String role = c.get("role", String.class);
        try {
            return Role.valueOf(role);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INVALID_TOKEN, "Invalid role");
        }
    }

    public Long validateAndGetUserIdFromRefreshToken(String refreshToken) {
        Claims c = parseClaims(refreshToken);
        String typ = c.get("typ", String.class);
        if (!"refresh".equals(typ)) {
            throw new ApiException(ErrorCode.INVALID_TOKEN, "Not a refresh token");
        }
        try {
            return Long.valueOf(c.getSubject());
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INVALID_TOKEN, "Invalid subject");
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new ApiException(ErrorCode.INVALID_TOKEN, "Invalid token");
        }
    }
}
