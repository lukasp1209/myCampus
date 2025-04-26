package com.example.my_campus_core.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeslot_id")
    private TimeSlot timeSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private UserEntity professor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "exam_all_students", joinColumns = @JoinColumn(name = "exam_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<UserEntity> allStudents = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "exam_enrolled_students", joinColumns = @JoinColumn(name = "exam_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<UserEntity> enrolledStudents = new ArrayList<>();

    private LocalDate examDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    public void setAllStudents(List<UserEntity> students) {
        this.allStudents.clear();
        if (students != null) {
            this.allStudents.addAll(students);
        }
    }

    public void setEnrolledStudents(List<UserEntity> students) {
        this.enrolledStudents.clear();
        if (students != null) {
            this.enrolledStudents.addAll(students);
        }
    }
}
