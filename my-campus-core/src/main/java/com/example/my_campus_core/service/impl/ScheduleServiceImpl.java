package com.example.my_campus_core.service.impl;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Lecture;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.service.ScheduleService;
import com.example.my_campus_core.service.impl.schedule.ScheduleSolution;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private static final Integer SCHEDULE_ID = 1;

    private CourseRepository courseRepository;

    private SolverManager<ScheduleSolution, Integer> solverManager; // Assuming you have a SolverManager bean configured

    public ScheduleServiceImpl(CourseRepository courseRepository,
            SolverManager<ScheduleSolution, Integer> solverManager) {
        this.courseRepository = courseRepository;
        this.solverManager = solverManager;
    }

    @Override
    public ScheduleSolution generateScheduleSolution(List<Course> courses, List<Room> rooms, List<TimeSlot> timeSlots) {
        List<Course> courseList = courseRepository.findAll();
        List<Room> roomList = new ArrayList<>();
        roomList.add(new Room(1, "Room A"));
        roomList.add(new Room(2, "Room B"));
        roomList.add(new Room(3, "Room C"));
        List<TimeSlot> timeSlotList = new ArrayList<>();
        timeSlotList.add(new TimeSlot(1, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)));
        timeSlotList.add(new TimeSlot(2, DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)));
        timeSlotList.add(new TimeSlot(3, DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(12, 0)));
        timeSlotList.add(new TimeSlot(4, DayOfWeek.THURSDAY, LocalTime.of(12, 0), LocalTime.of(13, 0)));
        timeSlotList.add(new TimeSlot(5, DayOfWeek.FRIDAY, LocalTime.of(13, 0), LocalTime.of(14, 0)));

        // Generate a Lecture for each Course (can generate more if needed)
        List<Lecture> lectures = courseList.stream()
                .map(course -> {
                    Lecture lecture = new Lecture();
                    lecture.setId(course.getId()); // Assuming Lecture has an ID field
                    lecture.setCourse(course);
                    lecture.setRoom(roomList.get((int) (Math.random() * roomList.size()))); // Randomly assign a room
                    lecture.setTimeSlot(timeSlotList.get((int) (Math.random() * timeSlotList.size())));
                    lecture.setProfessor(course.getProfessor()); // Assuming Course has a getProfessor() method
                    return lecture;
                })
                .collect(Collectors.toList());

        ScheduleSolution unsolvedSchedule = new ScheduleSolution();
        unsolvedSchedule.setCourseList(courseList);

        unsolvedSchedule.setRoomList(roomList);
        unsolvedSchedule.setTimeSlotList(timeSlotList);
        unsolvedSchedule.setLectureList(lectures);
        List<UserEntity> professorList = new ArrayList<>();
        for (Course course : courseList) {
            if (!professorList.contains(course.getProfessor())) {
                professorList.add(course.getProfessor());
            }
        }
        unsolvedSchedule.setProfessorList(professorList);

        // Solve the schedule
        ScheduleSolution solvedSchedule = null;
        try {
            solvedSchedule = solverManager.solveAndListen(
                    SCHEDULE_ID,
                    id -> unsolvedSchedule,
                    bestSolution -> {
                        // You can persist or process results here
                        System.out.println("New best solution found with score: " + bestSolution.getScore());
                    }).getFinalBestSolution();
        } catch (InterruptedException | ExecutionException e) {
            // Handle the exceptions (e.g., log the error or rethrow as a runtime exception)
            e.printStackTrace();
            throw new RuntimeException("Error while solving the schedule", e);
        }

        System.out.println("Final best solution found with score: " + solvedSchedule);
        return solvedSchedule;
    }

}
