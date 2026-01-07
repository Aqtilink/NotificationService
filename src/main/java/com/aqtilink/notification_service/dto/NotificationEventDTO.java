package com.aqtilink.notification_service.dto;

import java.util.UUID;

public class NotificationEventDTO {

    private UUID id;
    private String email;
    private String subject;
    private String message;

    public NotificationEventDTO() {}

    public String getEmail() {return email;}
    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}
    public void setEmail(String email) {this.email = email;}
    public String getSubject() {return subject;}
    public void setSubject(String subject) {this.subject = subject;}
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
}
