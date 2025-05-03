package com.example.notifications_service.controllers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.notifications_service.config.RabbitMQConfig;
import com.example.notifications_service.dto.NotificationConsumerDto;
import com.example.notifications_service.service.NotificationService;

@Component
public class NotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void recieveNotification(NotificationConsumerDto notificationConsumerDto) {

        notificationService.newNotification(notificationConsumerDto);
    }
}
