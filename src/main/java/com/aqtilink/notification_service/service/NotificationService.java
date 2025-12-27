package com.aqtilink.notification_service.service;

import com.aqtilink.notification_service.dto.NotificationEventDTO;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void send(NotificationEventDTO dto) {
        System.out.println("EMAIL SENT");
        System.out.println("To: " + dto.getEmail());
        System.out.println("Subject: " + dto.getSubject());
        System.out.println("Message: " + dto.getMessage());
    }
}
