package com.example.resourceservice.config;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/user/**").hasRole("USER")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/privilege/**").hasAuthority("WRITE_PRIVILEGE")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String secret = "my-super-secret-key-for-demo-purpose-which-is-long-enough";
        return NimbusJwtDecoder.withSecretKey(new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA256")).build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<String> roles = jwt.getClaimAsStringList("roles");
            Collection<String> privileges = jwt.getClaimAsStringList("privileges");
            List<GrantedAuthority> authorities = List.of();
            if (roles != null) {
                authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            }
            if (privileges != null) {
                authorities = List.<GrantedAuthority>copyOf(authorities);
                authorities = new java.util.ArrayList<>(authorities);
                authorities.addAll(privileges.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            }
            return authorities;
        });
        return converter;
    }
}
