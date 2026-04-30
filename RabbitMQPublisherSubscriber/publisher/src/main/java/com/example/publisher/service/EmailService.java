package com.example.publisher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendFallbackEmail(String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("admin@example.com"); // Replace with actual email
        mailMessage.setSubject("RabbitMQ Publish Failed - Fallback Notification");
        mailMessage.setText("Failed to publish message to RabbitMQ. Message: " + message);

        mailSender.send(mailMessage);
    }
}