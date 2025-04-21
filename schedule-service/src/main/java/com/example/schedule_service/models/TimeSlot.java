package com.example.schedule_service.models;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.Data;

@Data
public class TimeSlot {
    private int id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
