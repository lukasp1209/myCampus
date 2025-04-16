package com.example.my_campus_core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_campus_core.models.Lecture;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    List<Lecture> findByCourseId(int courseId);
} 