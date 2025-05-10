package com.example.my_campus_core.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_campus_core.models.Lecture;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    List<Lecture> findTop3ByProfessor_IdAndDateGreaterThanEqualOrderByDateAsc(Integer professorId, LocalDate date);

    List<Lecture> findTop3ByAllStudents_IdAndDateGreaterThanEqualOrderByDateAsc(
            Integer studentId,
            LocalDate date);

    List<Lecture> findAllByCourse_Id(int courseId);
}
