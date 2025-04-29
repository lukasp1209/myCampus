package com.example.my_campus_core.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_campus_core.models.Exam;
import com.example.my_campus_core.models.Lecture;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    boolean existsByCourseId(Integer courseId);

    Exam findByCourseId(int courseId);

    List<Exam> findAllByProfessorId(int professorId);

    List<Exam> findTop3ByProfessor_IdAndExamDateGreaterThanEqualOrderByExamDateAsc(Integer professorId, LocalDate date);

    List<Exam> findTop3ByAllStudents_IdAndExamDateGreaterThanEqualOrderByExamDateAsc(
            Integer studentId,
            LocalDate date);
}
