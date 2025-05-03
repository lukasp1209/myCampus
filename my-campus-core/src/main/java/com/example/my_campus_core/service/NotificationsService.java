package com.example.my_campus_core.service;

import java.time.LocalDate;
import java.time.LocalTime;

public interface NotificationsService {
    void sendNotification(String message, int userId);
}
