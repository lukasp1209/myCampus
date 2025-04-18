package com.example.my_campus_core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_campus_core.models.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByStudentsId(int studentId);

    List<Course> findByProfessorId(int professorId);

    List<Course> findByNameContainingIgnoreCase(String name);

    // Custom query methods can be defined here if needed
    // For example, findByProfessorId, findByStudentId, etc.

}
