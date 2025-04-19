package com.example.schedule_service.dto.request;

import java.util.List;

import com.example.schedule_service.models.Course;
import com.example.schedule_service.models.Room;
import com.example.schedule_service.models.TimeSlot;

import lombok.Data;

@Data
public class RequestDto {
    List<Room> rooms;
    List<Course> courses;
    List<TimeSlot> timeSlots;
}
