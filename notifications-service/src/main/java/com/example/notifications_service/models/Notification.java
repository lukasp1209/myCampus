package com.example.notifications_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Notification {
    public enum Status {
        READ,
        NOT_READ
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;
    private String message;
    @Enumerated(EnumType.STRING) // This stores the enum value as a string in the database
    private Status status = Status.NOT_READ;
}
