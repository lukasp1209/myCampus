package com.example.schedule_service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.schedule_service.dto.request.RequestDto;
import com.example.schedule_service.models.Course;
import com.example.schedule_service.models.Lecture;
import com.example.schedule_service.models.Room;
import com.example.schedule_service.models.ScheduleSolution;
import com.example.schedule_service.models.TimeSlot;
import com.example.schedule_service.models.User;
import com.example.schedule_service.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private static Integer SCHEDULE_ID = 1;
    private SolverManager<ScheduleSolution, Integer> solverManager;

    @Autowired
    public ScheduleServiceImpl(SolverManager<ScheduleSolution, Integer> solverManager) {
        this.solverManager = solverManager;
    }

    @Override
    public ScheduleSolution generate(RequestDto requestDto) {
        System.out.println("CAME INTO GENERATE METHOD");
        List<Course> courseList = requestDto.getCourses();
        List<Room> roomList = requestDto.getRooms();
        List<TimeSlot> timeSlotList = requestDto.getTimeSlots();
        List<Lecture> lectures = new ArrayList<>();
        int lectureId = 0; // Starting ID value - adjust if needed

        for (Course course : courseList) {
            Lecture lecture = new Lecture();
            lecture.setId(lectureId++); // Set ID and increment
            lecture.setCourse(course);
            lecture.setRoom(roomList.get((int) (Math.random() * roomList.size())));
            lecture.setTimeSlot(timeSlotList.get((int) (Math.random() * timeSlotList.size())));
            lecture.setProfessor(course.getProfessor());
            lectures.add(lecture);
        }

        ScheduleSolution unsolvedSchedule = new ScheduleSolution();
        unsolvedSchedule.setCourseList(courseList);
        unsolvedSchedule.setRoomList(roomList);
        unsolvedSchedule.setTimeSlotList(timeSlotList);
        unsolvedSchedule.setLectureList(lectures);
        List<User> professorList = new ArrayList<>();
        for (Course course : courseList) {
            if (!professorList.contains(course.getProfessor())) {
                professorList.add(course.getProfessor());
            }
        }
        unsolvedSchedule.setProfessorList(professorList);
        System.out.println("GENERATED UNSOLVED SCHEDULE" + unsolvedSchedule);
        // Solve the schedule
        ScheduleSolution solvedSchedule = null;
        try {
            System.out.println("STARTING TO SOLVE");
            solvedSchedule = solverManager.solveAndListen(
                    SCHEDULE_ID,
                    id -> unsolvedSchedule,
                    bestSolution -> {
                        // You can persist or process results here
                        System.out.println("New best solution found with score: " + bestSolution.getScore());
                    }).getFinalBestSolution();
            System.out.println("SHOULD BE SOLVED");
        } catch (InterruptedException | ExecutionException e) {
            // Handle the exceptions (e.g., log the error or rethrow as a runtime exception)
            e.printStackTrace();
            throw new RuntimeException("Error while solving the schedule", e);
        }

        System.out.println("Final best solution found with score: " + solvedSchedule);
        SCHEDULE_ID = SCHEDULE_ID + 1;
        // return solvedSchedule;
        return solvedSchedule;
    }

}
