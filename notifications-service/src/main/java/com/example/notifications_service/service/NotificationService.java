package com.example.notifications_service.service;

import java.util.List;

import com.example.notifications_service.dto.NotificationConsumerDto;
import com.example.notifications_service.dto.NotificationDto;
import com.example.notifications_service.models.Notification;

public interface NotificationService {

    void newNotification(NotificationConsumerDto notificationDto);

    List<Notification> getNotifcationsForUser(Integer userId);
}
