package com.example.notifications_service.dto;

import lombok.Data;

@Data
public class NotificationConsumerDto {
    private int userId;
    private String message;
}
