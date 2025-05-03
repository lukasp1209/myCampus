package com.example.notifications_service.controllers.api.notification.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.notifications_service.dto.NotificationDto;
import com.example.notifications_service.models.Notification;
import com.example.notifications_service.service.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/notification/v1/")
public class NotificationControllerREST {
    private NotificationService notificationService;

    @Autowired
    public NotificationControllerREST(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("{userId}")
    public ResponseEntity getMethodName(@PathVariable int userId) {
        return new ResponseEntity<>(notificationService.getNotifcationsForUser(userId), HttpStatus.OK);
    }

}
