package com.aqtilink.notification_service.service;

import com.aqtilink.notification_service.dto.NotificationEventDTO;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    // Mock implementation of sending an email notification -> to be replaced with real email service integration
    // Probably SendGrid, but i have no energy right now to struggle with it

    public void send(NotificationEventDTO dto) {
        System.out.println("-------------------------------------------------");
        System.out.println("|--SENDING E-MAIL TO: " + dto.getEmail());
        System.out.println("|--SUBJECT: " + dto.getSubject());
        System.out.println("|--MESSAGE: " + dto.getMessage());
        System.out.println("-------------------------------------------------");
    }
}
