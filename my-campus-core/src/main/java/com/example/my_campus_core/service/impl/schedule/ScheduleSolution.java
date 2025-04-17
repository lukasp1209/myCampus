package com.example.my_campus_core.service.impl.schedule;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Lecture;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.models.UserEntity;

import lombok.Data;

@Data
@PlanningSolution
public class ScheduleSolution {
    @ValueRangeProvider(id = "timeSlotRange")
    private List<TimeSlot> timeSlotList;

    @ValueRangeProvider(id = "roomRange")
    private List<Room> roomList;

    private List<Course> courseList;

    private List<UserEntity> professorList;

    @PlanningEntityCollectionProperty
    private List<Lecture> lectureList;

    @PlanningScore
    private HardSoftScore score;
}
