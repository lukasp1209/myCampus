package com.example.my_campus_core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_campus_core.models.Exam;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    boolean existsByCourseId(Integer courseId);

    Exam findByCourseId(int courseId);

    List<Exam> findAllByProfessorId(int professorId);
}
