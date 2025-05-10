package com.example.schedule_service.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import com.example.schedule_service.models.Lecture;
import com.example.schedule_service.models.User;

public class ScheduleConstraintProvider implements ConstraintProvider {

        @Override
        public Constraint[] defineConstraints(ConstraintFactory factory) {
                return new Constraint[] {
                                roomConflict(factory),
                                professorConflict(factory),
                                studentConflict(factory)
                };
        }

        private Constraint roomConflict(ConstraintFactory factory) {
                return factory
                                .forEachUniquePair(Lecture.class,
                                                Joiners.equal(Lecture::getTimeSlot),
                                                Joiners.equal(Lecture::getRoom))
                                .penalize("Room conflict", HardSoftScore.ONE_HARD);
        }

        private Constraint professorConflict(ConstraintFactory factory) {
                return factory
                                .forEachUniquePair(Lecture.class,
                                                Joiners.equal(Lecture::getTimeSlot),
                                                Joiners.equal(Lecture::getProfessors))
                                .penalize("Professor conflict", HardSoftScore.ONE_HARD);
        }

        private Constraint studentConflict(ConstraintFactory factory) {
                return factory
                                .forEachUniquePair(Lecture.class,
                                                Joiners.equal(Lecture::getTimeSlot))
                                .filter((c1, c2) -> {
                                        Set<Integer> c1StudentIds = c1.getStudents().stream().map(User::getId)
                                                        .collect(Collectors.toSet());
                                        for (User student : c2.getStudents()) {
                                                if (c1StudentIds.contains(student.getId()))
                                                        return true;
                                        }
                                        return false;
                                })
                                .penalize("Student conflict", HardSoftScore.ONE_HARD);
        }
}
