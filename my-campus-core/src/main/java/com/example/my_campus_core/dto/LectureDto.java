package com.example.my_campus_core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.models.UserEntity;

import lombok.Data;

@Data
public class LectureDto {
    private int id;
    private Course course;
    private TimeSlot timeSlot;
    private Room room;
    private UserDto professor;
    private List<UserDto> allStudents = new ArrayList<>();
    private List<UserDto> attendedStudents = new ArrayList<>();
    private LocalDate date;
}
