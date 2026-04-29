# Authorization Server Spring Security

This project implements an industry-style authorization server using Spring Boot and Spring Security.

## Modules

- `auth-server`: Spring Boot authorization server with JWT access tokens, refresh tokens, registration, login, logout, token validation, role and privilege claims.
- `resource-service`: Spring Boot protected resource service validating JWT tokens from the authorization server.

## Features

- User registration and login
- JWT access token generation with `roles` and `privileges`
- Refresh token lifecycle and token refresh endpoint
- Token validation endpoint
- Logout revoking refresh tokens
- Admin user seeded at startup: `admin@spring.com` / `Admin@123`
- H2 in-memory database for demo purposes

## Run the services

From `AuthorizationServerSpringSecurity` folder:

```bash
mvn -pl auth-server -am spring-boot:run
```

In another terminal:

```bash
mvn -pl resource-service -am spring-boot:run
```

## Demo endpoints

### Auth server

- `POST http://localhost:8080/api/auth/register`
- `POST http://localhost:8080/api/auth/login`
- `POST http://localhost:8080/api/auth/refresh`
- `POST http://localhost:8080/api/auth/logout`
- `GET http://localhost:8080/api/auth/validate`

### Resource service

- `GET http://localhost:8081/api/public/hello`
- `GET http://localhost:8081/api/user/profile`
- `GET http://localhost:8081/api/admin/dashboard`
- `GET http://localhost:8081/api/privilege/write`

## Notes

- Access tokens are signed with an HMAC key and include roles/privileges in claims.
- Refresh tokens are stored and revoked on logout.
- The H2 console is available at `http://localhost:8080/h2-console`.

