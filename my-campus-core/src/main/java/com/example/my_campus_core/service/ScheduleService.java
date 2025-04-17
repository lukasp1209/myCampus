package com.example.my_campus_core.service;

import java.util.List;

import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.service.impl.schedule.ScheduleSolution;

public interface ScheduleService {
    ScheduleSolution generateScheduleSolution(List<Course> courses, List<Room> rooms, List<TimeSlot> timeSlots);
}
