package com.example.notifications_service.controllers.ws.notification.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.notifications_service.models.Notification;

public class NotificationControllerWS {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/application")
    @SendTo("/all/messages")
    public Notification send(final Notification notification) throws Exception {
        return notification;
    }

    @MessageMapping("/private")
    public void sendNotifcation(@Payload Notification notification) {
        simpMessagingTemplate.convertAndSend( "/specific", notification);
    }
}
