package com.example.my_campus_core.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.dto.ExamDto;
import com.example.my_campus_core.dto.request.ExamRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.exceptions.InternalErrorException;
import com.example.my_campus_core.exceptions.NotFoundException;
import com.example.my_campus_core.models.Exam;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.repository.ExamRepository;
import com.example.my_campus_core.repository.RoomRepository;
import com.example.my_campus_core.repository.TimeSlotRepository;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.ExamService;
import com.example.my_campus_core.service.ScheduleService;
import com.example.my_campus_core.util.Mappers;

@Service
public class ExamServiceImpl implements ExamService {
    private CourseRepository courseRepository;
    private RoomRepository roomRepository;
    private TimeSlotRepository timeSlotRepository;
    private ExamRepository examRepository;
    private ScheduleService scheduleService;
    private Mappers mapper;

    @Autowired
    public ExamServiceImpl(CourseRepository courseRepository,
            UserRepository userRepository,
            RoomRepository roomRepository,
            TimeSlotRepository timeSlotRepository,
            ExamRepository examRepository,
            ScheduleService scheduleService) {
        this.courseRepository = courseRepository;
        this.roomRepository = roomRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.examRepository = examRepository;
        this.scheduleService = scheduleService;
    }

    @Override
    public ResponseDto createNewExam(ExamRequestDto examRequestDto) {
        ResponseDto responseDto = new ResponseDto();
        Exam newExam = new Exam();
        newExam.setCourse(courseRepository.findById(examRequestDto.getCourseId())
                .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong! \\n" + //
                        " Please try again or contact platform support!")));
        newExam.setRoom(roomRepository.findById(examRequestDto.getRoomId())
                .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong! \\n" + //
                        " Please try again or contact platform support!")));
        newExam.setTimeSlot(timeSlotRepository.findById(examRequestDto.getExamTime())
                .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong! \\n" + //
                        " Please try again or contact platform support!")));
        newExam.setProfessor(newExam.getCourse().getProfessor());
        newExam.setAllStudents(newExam.getCourse().getStudents());
        newExam.setExamDate(LocalDate.parse(examRequestDto.getExamDate()));

        Exam savedExam = examRepository.save(newExam);
        scheduleService.addExamToSchedule(examRequestDto.getExamDate(), savedExam);
        return responseDto;
    }

    @Override
    public boolean examForCourseExists(int courseId) {
        return examRepository.existsByCourseId(courseId);
    }

    @Override
    public ExamDto getExamCourseId(int courseId) {
        if (examForCourseExists(courseId)) {
            ExamDto examDto = mapper.examToExamDto(examRepository.findByCourseId(courseId));
            return examDto;
        }
        return new ExamDto();
    }

    @Override
    public List<ExamDto> getAllExamsForProfessorWithId(int professorId) {
        List<ExamDto> examDtos = examRepository.findAllByProfessorId(professorId).stream()
                .map(exam -> mapper.examToExamDto(exam)).collect(Collectors.toList());

        return examDtos;
    }

    @Override
    public ExamDto getExamById(int examId) {
        return mapper.examToExamDto(examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam with ID:" + examId + " does not exist")));
    }

}
