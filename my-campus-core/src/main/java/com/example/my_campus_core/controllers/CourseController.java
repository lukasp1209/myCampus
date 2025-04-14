package com.example.my_campus_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.service.CourseService;
import com.example.my_campus_core.service.UserService;

@Controller
public class CourseController {

    private CourseService courseService;
    private UserService userService;

    @Autowired
    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/course/managment")
    public String getCourseManagmentPage() {
        return "./courseManagment"; // Return the name of the course management view (e.g., courseManagment.html)
    }

    @GetMapping("/course/add")
    public String getAddCoursePage() {
        return "./addCourse"; // Return the name of the add course view (e.g., addCourse.html)
    }

    @PostMapping("/course/add")
    public String addCourse(@ModelAttribute CourseDto courseDto) {
        courseService.addCourse(courseDto); // Call the service to add the course
        System.out.println(courseDto);

        return "redirect:/course/managment"; // Redirect to the course management page after adding a course
    }

}
