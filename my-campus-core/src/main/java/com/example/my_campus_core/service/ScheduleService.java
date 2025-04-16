package com.example.my_campus_core.service;

import java.util.List;

import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Lecture;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.service.impl.schedule.ScheduleSolution;

public interface ScheduleService {
    /**
     * Generates an optimal schedule for the given courses, rooms, and time slots
     * @param courses List of courses to schedule
     * @param rooms List of available rooms
     * @param timeSlots List of available time slots
     * @return A solution containing the scheduled lectures
     */
    ScheduleSolution generateSchedule(List<Course> courses, List<Room> rooms, List<TimeSlot> timeSlots);

    /**
     * Gets all lectures for a specific course
     * @param courseId The ID of the course
     * @return List of lectures for the course
     */
    List<Lecture> getLecturesForCourse(int courseId);

    /**
     * Gets all lectures for a specific professor
     * @param professorId The ID of the professor
     * @return List of lectures for the professor
     */
    List<Lecture> getLecturesForProfessor(int professorId);

    /**
     * Gets all lectures for a specific student
     * @param studentId The ID of the student
     * @return List of lectures for the student
     */
    List<Lecture> getLecturesForStudent(int studentId);
} 