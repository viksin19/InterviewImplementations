# InterviewImplementations

A collection of Spring Boot microservices implementations demonstrating industry best practices for common interview scenarios.

## Projects

### [Authorization Server Spring Security](./AuthorizationServerSpringSecurity/)
A complete OAuth2 authorization server implementation with JWT tokens, user management, and role-based access control.

**Features:**
- JWT access and refresh token management
- User registration and authentication
- Role and privilege-based authorization
- Token validation and logout
- H2 in-memory database

**Ports:** Auth Server (8080), Resource Service (8081)

### [RabbitMQ Publisher and Consumer](./RabbitMQPublisherSubscriber/)
Asynchronous messaging microservices for order update notifications with robust error handling and fallback mechanisms.

**Features:**
- Publisher service with RabbitMQ integration
- Consumer service with retry and exponential backoff
- Fallback to database storage and email notifications
- Message acknowledgment and dead letter handling

**Ports:** Publisher (8081), Consumer (8082)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- RabbitMQ (for messaging project)

## Getting Started

1. Clone the repository
2. Navigate to the desired project directory
3. Follow the specific README instructions for each project

## Technologies Used

- Spring Boot 3.x
- Spring Security
- Spring AMQP (RabbitMQ)
- Spring Data JPA
- H2 Database
- JWT
- Maven

## Contributing

This repository contains implementations for common interview questions and scenarios. Each project demonstrates production-ready patterns and best practices.