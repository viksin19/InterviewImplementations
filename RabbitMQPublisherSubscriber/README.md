# RabbitMQ Publisher and Consumer Microservices

This project contains two Spring Boot microservices: a Publisher and a Consumer for handling order update notifications via RabbitMQ.

## Architecture

- **Publisher Service** (Port 8081): Publishes order update messages to RabbitMQ exchange.
- **Consumer Service** (Port 8082): Consumes messages from RabbitMQ queue and processes them.

## Features

- Asynchronous messaging with RabbitMQ
- Fallback mechanisms for reliability
- Industry-standard coding practices
- Message acknowledgment
- Retry with exponential backoff for consumer

## Prerequisites

- Java 17
- Maven
- RabbitMQ server running on localhost:5672 (default credentials: guest/guest)

## Running the Services

1. Start RabbitMQ (if not already running):
   ```bash
   # Using Docker
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management
   ```

2. Start the Publisher service:
   ```bash
   cd publisher
   mvn spring-boot:run
   ```

3. Start the Consumer service:
   ```bash
   cd consumer
   mvn spring-boot:run
   ```

## API Usage

### Publish Order Update

```bash
curl -X POST http://localhost:8081/api/notifications/order-update \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "12345",
    "status": "SHIPPED"
  }'
```

## Fallback Mechanisms

### Publisher Fallback
- If RabbitMQ is unavailable, messages are stored in H2 database
- Email notification is sent to admin

### Consumer Fallback
- Failed message processing triggers retry with exponential backoff (up to 3 attempts)
- After max retries, message is logged (can be extended to dead letter queue)

## Configuration

Update `application.yml` files for custom RabbitMQ settings, email configuration, etc.

## Monitoring

- Publisher: Check H2 console at http://localhost:8081/h2-console
- RabbitMQ Management: http://localhost:15672 (guest/guest)