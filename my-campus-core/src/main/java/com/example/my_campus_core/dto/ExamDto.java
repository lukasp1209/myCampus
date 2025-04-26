package com.example.my_campus_core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.models.UserEntity;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class ExamDto {
    private int id;
    private CourseDto course;
    private TimeSlot timeSlot;
    private RoomDto room;
    private UserDto professor;
    private List<UserDto> allStudents;
    private List<UserDto> enrolledStudents;
    private LocalDate examDate;
    private String description;

}
