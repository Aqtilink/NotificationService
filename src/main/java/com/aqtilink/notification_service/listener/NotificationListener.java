package com.aqtilink.notification_service.listener;

import com.aqtilink.notification_service.dto.NotificationEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.aqtilink.notification_service.service.NotificationService;

// Listener for processing notification events from the message queue

@Component
public class NotificationListener {

    private final NotificationService service;

    public NotificationListener(NotificationService service) {
        this.service = service;
    }

    @RabbitListener(queues = "notification-queue")
    public void receive(NotificationEventDTO dto) {
        service.send(dto);
    }
}
