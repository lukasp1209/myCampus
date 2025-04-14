package com.example.my_campus_core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;
    private UserRepository userRepository;

    public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addCourse(CourseDto courseDto) {
        Course course = new Course();
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());
        course.setProfessor(userRepository.findById(courseDto.getProfessorId()).orElse(null));
        List<UserEntity> students = new ArrayList<>();
        for (int studentId : courseDto.getStudentsIds()) {
            System.out.println("Student ID: " + studentId);
            UserEntity student = userRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("User with ID " + studentId + " does not exist"));
            students.add(student);
        }
        course.setStudents(students);
        courseRepository.save(course);
    }

}
