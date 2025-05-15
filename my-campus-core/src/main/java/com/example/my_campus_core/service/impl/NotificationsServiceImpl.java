package com.example.my_campus_core.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.config.RabbitMQConfig;
import com.example.my_campus_core.dto.Notification;
import com.example.my_campus_core.service.NotificationsService;

/**
 * Implementation of the NotificationsService interface that handles notification-related operations.
 * This service manages sending notifications to users through RabbitMQ.
 */
@Service
public class NotificationsServiceImpl implements NotificationsService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Sends a notification to a specific user through RabbitMQ.
     *
     * @param message The notification message to send
     * @param userId The ID of the user to send the notification to
     */
    @Override
    public void sendNotification(String message, int userId) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, notification);
    }

}
