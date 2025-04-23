package com.example.my_campus_core.service;

import java.util.List;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.response.ResponseDto;

public interface CourseService {
    void addCourse(CourseDto courseDto);

    List<CourseDto> getAllCourses(int page); // Method to retrieve all courses

    CourseDto getCourseById(int courseId); // Method to retrieve a course by its ID

    List<CourseDto> getCoursesByUserId(int studentId, String userRole); // Method to retrieve courses by student ID

    List<CourseDto> searchForCourses(String searchTerm); // Method to search for courses by name or
                                                         // description

    ResponseDto updateCourse(CourseDto courseDto);
}