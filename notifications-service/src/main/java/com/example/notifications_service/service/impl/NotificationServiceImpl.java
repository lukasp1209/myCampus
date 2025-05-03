package com.example.notifications_service.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.notifications_service.controllers.NotificationConsumer;
import com.example.notifications_service.dto.NotificationConsumerDto;
import com.example.notifications_service.dto.NotificationDto;
import com.example.notifications_service.models.Notification;
import com.example.notifications_service.repository.NotificationRepository;
import com.example.notifications_service.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void newNotification(NotificationConsumerDto notificationDto) {
        Notification notification = new Notification();
        notification.setUserId(notificationDto.getUserId());
        notification.setMessage(notificationDto.getMessage());
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotifcationsForUser(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications;
    }

    @Override
    public void notificationRead(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setStatus(Notification.Status.READ);
        notificationRepository.saveAndFlush(notification);
    }
}
