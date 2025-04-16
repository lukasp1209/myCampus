package com.example.my_campus_core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Lecture;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.repository.LectureRepository;
import com.example.my_campus_core.service.ScheduleService;
import com.example.my_campus_core.service.impl.schedule.ScheduleSolution;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final SolverManager<ScheduleSolution, Long> solverManager;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;

    @Autowired
    public ScheduleServiceImpl(SolverManager<ScheduleSolution, Long> solverManager,
                             CourseRepository courseRepository,
                             LectureRepository lectureRepository) {
        this.solverManager = solverManager;
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
    }

    @Override
    public ScheduleSolution generateSchedule(List<Course> courses, List<Room> rooms, List<TimeSlot> timeSlots) {
        // Create lectures for each course
        List<Lecture> lectures = new ArrayList<>();
        for (Course course : courses) {
            // Create one lecture per week for the course
            Lecture lecture = new Lecture();
            lecture.setCourse(course);
            lectures.add(lecture);
        }

        // Create the problem solution
        ScheduleSolution problem = new ScheduleSolution();
        problem.setCourseList(courses);
        problem.setRoomList(rooms);
        problem.setTimeSlotList(timeSlots);
        problem.setLectureList(lectures);

        // Solve the problem
        SolverJob<ScheduleSolution, Long> solverJob = solverManager.solve(1L, problem);
        ScheduleSolution solution;
        try {
            solution = solverJob.getFinalBestSolution();
        } catch (Exception e) {
            throw new RuntimeException("Failed to solve the scheduling problem", e);
        }

        // Save the scheduled lectures
        for (Lecture lecture : solution.getLectureList()) {
            lectureRepository.save(lecture);
        }

        return solution;
    }

    @Override
    public List<Lecture> getLecturesForCourse(int courseId) {
        return lectureRepository.findByCourseId(courseId);
    }

    @Override
    public List<Lecture> getLecturesForProfessor(int professorId) {
        List<Course> courses = courseRepository.findByProfessorId(professorId);
        return courses.stream()
                .flatMap(course -> lectureRepository.findByCourseId(course.getId()).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Lecture> getLecturesForStudent(int studentId) {
        List<Course> courses = courseRepository.findByStudentsId(studentId);
        return courses.stream()
                .flatMap(course -> lectureRepository.findByCourseId(course.getId()).stream())
                .collect(Collectors.toList());
    }
} 