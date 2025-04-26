package com.example.my_campus_core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.dto.request.ExamRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.models.Exam;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.repository.ExamRepository;
import com.example.my_campus_core.repository.RoomRepository;
import com.example.my_campus_core.repository.TimeSlotRepository;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.ExamService;
import com.example.my_campus_core.service.ScheduleService;

@Service
public class ExamServiceImpl implements ExamService {
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private TimeSlotRepository timeSlotRepository;
    private ExamRepository examRepository;
    private ScheduleService scheduleService;

    @Autowired
    public ExamServiceImpl(CourseRepository courseRepository,
            UserRepository userRepository,
            RoomRepository roomRepository,
            TimeSlotRepository timeSlotRepository,
            ExamRepository examRepository,
            ScheduleService scheduleService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.examRepository = examRepository;
        this.scheduleService = scheduleService;
    }

    @Override
    public ResponseDto createNewExam(ExamRequestDto examRequestDto) {
        ResponseDto responseDto = new ResponseDto();
        Exam newExam = new Exam();
        newExam.setCourse(courseRepository.findById(examRequestDto.getCourseId()).orElseThrow(null));
        newExam.setRoom(roomRepository.findById(examRequestDto.getRoomId()).orElseThrow(null));
        newExam.setTimeSlot(timeSlotRepository.findById(examRequestDto.getExamTime()).orElseThrow(null));
        newExam.setProfessor(newExam.getCourse().getProfessor());
        newExam.setAllStudents(newExam.getCourse().getStudents());

        Exam savedExam = examRepository.save(newExam);
        scheduleService.addExamToSchedule(examRequestDto.getExamDate(), savedExam);
        return responseDto;
    }

    @Override
    public boolean examForCourseExists(int courseId) {
        return examRepository.existsByCourseId(courseId);
    }

}
