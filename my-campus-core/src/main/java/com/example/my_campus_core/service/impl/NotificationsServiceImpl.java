package com.example.my_campus_core.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.config.RabbitMQConfig;
import com.example.my_campus_core.dto.Notification;
import com.example.my_campus_core.service.NotificationsService;

@Service
public class NotificationsServiceImpl implements NotificationsService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendNotification(String message, int userId) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, notification);
    }

}
