package com.example.schedule_service.models;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import lombok.Data;

@Data
@PlanningSolution
public class ScheduleSolution {
    @ValueRangeProvider(id = "timeSlotRange")
    private List<TimeSlot> timeSlotList;

    @ValueRangeProvider(id = "roomRange")
    private List<Room> roomList;

    private List<Course> courseList;

    private List<User> professorList;

    @PlanningEntityCollectionProperty
    private List<Lecture> lectureList;

    @PlanningScore
    private HardSoftScore score;

}
