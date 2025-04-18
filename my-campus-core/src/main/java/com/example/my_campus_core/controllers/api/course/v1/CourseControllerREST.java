package com.example.my_campus_core.controllers.api.course.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_campus_core.service.CourseService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/course")
public class CourseControllerREST {
    private CourseService courseService;

    public CourseControllerREST(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/search")
    public ResponseEntity getCourse(@RequestParam String search) {
        return new ResponseEntity(courseService.searchForCourses(search), HttpStatus.OK);
    }

}
