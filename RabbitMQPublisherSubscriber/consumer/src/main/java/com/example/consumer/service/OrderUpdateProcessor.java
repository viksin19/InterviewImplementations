package com.example.consumer.service;

import com.example.consumer.dto.OrderUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class OrderUpdateProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OrderUpdateProcessor.class);

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void processOrderUpdate(String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            OrderUpdate orderUpdate = objectMapper.readValue(message, OrderUpdate.class);
            logger.info("Processing order update: {}", orderUpdate);

            // Simulate processing
            if ("FAILED".equals(orderUpdate.getStatus())) {
                throw new RuntimeException("Simulated processing failure for status FAILED");
            }

            // Actual processing logic here, e.g., update database, send notifications, etc.
            logger.info("Order update processed successfully: {}", orderUpdate);

        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage());
            throw new RuntimeException("Processing failed", e); // Wrap to trigger retry
        }
    }

    @Recover
    public void recover(Exception e, String message) {
        logger.error("Failed to process message after retries: {}", message);
        // In a real scenario, you might move to a dead letter queue or log for manual intervention
        // For now, just log
    }
}