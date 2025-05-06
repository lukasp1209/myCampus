package com.example.my_campus_core.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.my_campus_core.models.Exam;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    boolean existsByCourseId(Integer courseId);

    Exam findByCourseId(int courseId);

    List<Exam> findAllByProfessorId(int professorId);

    List<Exam> findTop3ByProfessor_IdAndExamDateGreaterThanEqualOrderByExamDateAsc(Integer professorId, LocalDate date);

    List<Exam> findTop3ByAllStudents_IdAndExamDateGreaterThanEqualOrderByExamDateAsc(
            Integer studentId,
            LocalDate date);

    List<Exam> findAllByCourseId(int courseId);

    List<Exam> findAllByAllStudents_Id(int studentId);

    // Check if a student is enrolled in an exam
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Exam e JOIN e.enrolledStudents s " +
            "WHERE e.id = :examId AND s.id = :userId")
    boolean isStudentEnrolledInExam(@Param("userId") int userId,
            @Param("examId") int examId);
}
