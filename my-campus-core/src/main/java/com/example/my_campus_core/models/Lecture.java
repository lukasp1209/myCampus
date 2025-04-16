package com.example.my_campus_core.models;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.springframework.security.access.method.P;

import com.github.javaparser.ast.Generated;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@PlanningEntity
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PlanningId
    private int id;

    @ManyToOne
    private Course course; // The high-level course (subject, students, professor)

    @PlanningVariable(valueRangeProviderRefs = "timeSlotRange")
    @ManyToOne
    private TimeSlot timeSlot;

    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    @ManyToOne
    private Room room;

    public UserEntity getProfessor() {
        return course.getProfessor();
    }

    public List<UserEntity> getStudents() {
        return course.getStudents();
    }
}
