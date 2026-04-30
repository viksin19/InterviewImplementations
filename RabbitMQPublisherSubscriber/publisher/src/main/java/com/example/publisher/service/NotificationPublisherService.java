package com.example.publisher.service;

import com.example.publisher.dto.OrderUpdate;
import com.example.publisher.entity.FailedMessage;
import com.example.publisher.repository.FailedMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationPublisherService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FailedMessageRepository failedMessageRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public void publishOrderUpdate(OrderUpdate orderUpdate) {
        try {
            String message = objectMapper.writeValueAsString(orderUpdate);
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            logger.info("Published order update: {}", orderUpdate);
        } catch (AmqpException e) {
            logger.error("Failed to publish message to RabbitMQ: {}", e.getMessage());
            handleFallback(orderUpdate);
        } catch (Exception e) {
            logger.error("Error serializing message: {}", e.getMessage());
            handleFallback(orderUpdate);
        }
    }

    private void handleFallback(OrderUpdate orderUpdate) {
        try {
            String message = objectMapper.writeValueAsString(orderUpdate);
            FailedMessage failedMessage = new FailedMessage(message);
            failedMessageRepository.save(failedMessage);
            emailService.sendFallbackEmail(message);
            logger.info("Stored failed message in DB and sent email notification");
        } catch (Exception e) {
            logger.error("Failed to handle fallback: {}", e.getMessage());
        }
    }
}