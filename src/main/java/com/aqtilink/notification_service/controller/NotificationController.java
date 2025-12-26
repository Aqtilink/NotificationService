package com.aqtilink.notification_service.controller;

import com.aqtilink.notification_service.dto.NotificationEventDTO;
import com.aqtilink.notification_service.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public void send(@RequestBody NotificationEventDTO dto) {
        service.send(dto);
    }
}
