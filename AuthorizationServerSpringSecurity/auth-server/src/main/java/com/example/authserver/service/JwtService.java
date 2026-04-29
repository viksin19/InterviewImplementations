package com.example.authserver.service;

import com.example.authserver.entity.AppUser;
import com.example.authserver.entity.Privilege;
import com.example.authserver.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final Key secretKey;
    private final long accessTokenExpirationMillis;
    private final long refreshTokenExpirationMillis;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.access-token-expiration-ms}") long accessTokenExpirationMillis,
                      @Value("${security.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMillis) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
    }

    public String generateAccessToken(AppUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(user.getEmail())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(accessTokenExpirationMillis)))
            .claim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
            .claim("privileges", user.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(Privilege::getName)
                .distinct()
                .collect(Collectors.toList()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public Instant getAccessTokenExpiry() {
        return Instant.now().plusMillis(accessTokenExpirationMillis);
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            return !parseToken(token).getExpiration().before(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    public String generateRefreshToken(AppUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(user.getEmail())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(refreshTokenExpirationMillis)))
            .claim("type", "refresh")
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }
}
