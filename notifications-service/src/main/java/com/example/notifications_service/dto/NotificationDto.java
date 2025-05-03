package com.example.notifications_service.dto;

import lombok.Data;

@Data
public class NotificationDto {
    private int id;
    private int userId;
    private String message;
    private String Status;
}
