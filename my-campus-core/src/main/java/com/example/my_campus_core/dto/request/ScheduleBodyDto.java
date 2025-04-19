package com.example.my_campus_core.dto.request;

import java.util.List;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.models.TimeSlot;

import lombok.Data;

@Data
public class ScheduleBodyDto {
    List<RoomDto> rooms;
    List<CourseDto> courses;
    List<TimeSlot> timeSlots;
}
