package com.aqtilink.notification_service.listener;

import com.aqtilink.notification_service.dto.NotificationEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    @RabbitListener(queues = "notification-queue")
    public void receive(NotificationEventDTO dto) {
        System.out.println("Received notification: " + dto.getEmail() + " | " + dto.getMessage());
        // tukaj kasneje lahko kličeš email-service
    }
}
