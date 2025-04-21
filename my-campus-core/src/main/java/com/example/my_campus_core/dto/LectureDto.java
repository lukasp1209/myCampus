package com.example.my_campus_core.dto;

import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.TimeSlot;

import lombok.Data;

@Data
public class LectureDto {
    private int id;
    private Course course;
    private TimeSlot timeSlot;
    private Room room;
    private UserDto professor;
}
