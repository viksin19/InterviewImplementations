package com.example.publisher.controller;

import com.example.publisher.dto.OrderUpdate;
import com.example.publisher.service.NotificationPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationPublisherService publisherService;

    @PostMapping("/order-update")
    public ResponseEntity<String> publishOrderUpdate(@RequestBody OrderUpdate orderUpdate) {
        publisherService.publishOrderUpdate(orderUpdate);
        return ResponseEntity.ok("Order update published successfully");
    }
}