package com.example.authserver.controller;

import com.example.authserver.dto.AuthRequest;
import com.example.authserver.dto.AuthResponse;
import com.example.authserver.dto.RefreshTokenRequest;
import com.example.authserver.dto.RegisterRequest;
import com.example.authserver.entity.AppUser;
import com.example.authserver.entity.RefreshToken;
import com.example.authserver.repository.RefreshTokenRepository;
import com.example.authserver.service.JwtService;
import com.example.authserver.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService,
                          JwtService jwtService,
                          RefreshTokenRepository refreshTokenRepository,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is already in use"));
        }

        AppUser user = new AppUser(request.getEmail(), request.getPassword());
        userService.createUser(user, request.getRoles() == null || request.getRoles().isEmpty()
            ? new HashSet<>(Collections.singletonList("ROLE_USER"))
            : request.getRoles());

        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        AppUser user = userService.findByEmail(request.getEmail());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(new RefreshToken(refreshToken, user, Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000L)));

        AuthResponse response = new AuthResponse(accessToken, refreshToken, jwtService.getAccessTokenExpiry());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token required"));
        }

        var tokenOptional = refreshTokenRepository.findByToken(request.getRefreshToken())
            .filter(token -> token.getExpiryDate().isAfter(Instant.now()));

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token"));
        }

        RefreshToken token = tokenOptional.get();
        AppUser user = token.getUser();
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        refreshTokenRepository.delete(token);
        refreshTokenRepository.save(new RefreshToken(newRefreshToken, user, Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000L)));

        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken, jwtService.getAccessTokenExpiry()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token required"));
        }

        refreshTokenRepository.findByToken(request.getRefreshToken())
            .ifPresent(refreshTokenRepository::delete);

        return ResponseEntity.ok(Map.of("message", "Successfully logged out"));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Bearer token required"));
        }

        String token = authorizationHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
        }

        Claims claims = jwtService.parseToken(token);
        return ResponseEntity.ok(Map.of(
            "subject", claims.getSubject(),
            "roles", claims.get("roles"),
            "privileges", claims.get("privileges"),
            "expiresAt", claims.getExpiration().getTime()
        ));
    }
}
