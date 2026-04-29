package com.example.resourceservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ResourceController {

    @GetMapping("/public/hello")
    public ResponseEntity<?> publicHello() {
        return ResponseEntity.ok(Map.of("message", "Hello from the public endpoint"));
    }

    @GetMapping("/user/profile")
    public ResponseEntity<?> userProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
            "message", "Protected user profile",
            "user", jwt.getSubject(),
            "roles", jwt.getClaimAsStringList("roles"),
            "privileges", jwt.getClaimAsStringList("privileges")
        ));
    }

    @GetMapping("/admin/dashboard")
    public ResponseEntity<?> adminDashboard(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
            "message", "Admin dashboard data",
            "user", jwt.getSubject(),
            "roles", jwt.getClaimAsStringList("roles"),
            "privileges", jwt.getClaimAsStringList("privileges")
        ));
    }

    @GetMapping("/privilege/write")
    public ResponseEntity<?> writePrivilege(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
            "message", "Access granted for WRITE_PRIVILEGE",
            "user", jwt.getSubject(),
            "privileges", jwt.getClaimAsStringList("privileges")
        ));
    }
}
