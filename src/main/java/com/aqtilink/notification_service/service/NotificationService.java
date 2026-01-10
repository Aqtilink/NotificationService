package com.aqtilink.notification_service.service;

import com.aqtilink.notification_service.dto.NotificationEventDTO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.Mail;

import java.io.IOException;

@Service
public class NotificationService {
    private final SendGrid sendGrid;

    public NotificationService(@Value("${sendgrid.api-key}") String sendGridApiKey) {
        this.sendGrid = new SendGrid(sendGridApiKey);
        //this.sendGrid.setDataResidency("eu");
    }

    // Mock implementation of sending an email notification -> to be replaced with real email service integration
    // Probably SendGrid, but i have no energy right now to struggle with it

    public void send(NotificationEventDTO dto) throws IOException {
        System.out.println("\n\n");
        System.out.println("Content of DTO: " + dto.getMessage());
        System.out.println("Sending notification to email: " + dto.getEmail());
        System.out.println("\n\n");

        Email from = new Email("noreply@em5604.aqtilink.live");
        String subject = dto.getSubject();
        Email to = new Email(dto.getEmail());
        Content content = new Content("text/plain", dto.getMessage());
        Mail mail = new Mail(from, subject, to, content);
        
        Request request = new Request();
        try {
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sendGrid.api(request);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
        System.out.println(response.getHeaders());
        } catch (IOException ex) {
        throw ex;
        }
    
    }
}
