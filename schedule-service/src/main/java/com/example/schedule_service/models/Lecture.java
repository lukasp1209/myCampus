package com.example.schedule_service.models;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PlanningEntity
public class Lecture {

    @PlanningId
    private int id;

    private Course course;

    @PlanningVariable(valueRangeProviderRefs = "timeSlotRange")
    private TimeSlot timeSlot;

    @PlanningVariable(valueRangeProviderRefs = "roomRange")

    private Room room;

    private User professor;

    public User getProfessors() {
        return course.getProfessor();
    }

    public List<User> getStudents() {
        return course.getStudents();
    }
}
