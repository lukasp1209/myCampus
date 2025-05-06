package com.example.my_campus_core.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.my_campus_core.dto.ExamDto;
import com.example.my_campus_core.dto.request.ExamRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.exceptions.InternalErrorException;
import com.example.my_campus_core.exceptions.NotFoundException;
import com.example.my_campus_core.models.Exam;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.repository.ExamRepository;
import com.example.my_campus_core.repository.RoomRepository;
import com.example.my_campus_core.repository.TimeSlotRepository;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.ExamService;
import com.example.my_campus_core.service.NotificationsService;
import com.example.my_campus_core.service.ScheduleService;
import com.example.my_campus_core.util.Mappers;

@Service
public class ExamServiceImpl implements ExamService {
    private CourseRepository courseRepository;
    private RoomRepository roomRepository;
    private TimeSlotRepository timeSlotRepository;
    private ExamRepository examRepository;
    private ScheduleService scheduleService;
    private UserRepository userRepository;
    private Mappers mapper;
    private NotificationsService notificationsService;

    @Autowired
    public ExamServiceImpl(CourseRepository courseRepository,
            UserRepository userRepository,
            RoomRepository roomRepository,
            TimeSlotRepository timeSlotRepository,
            ExamRepository examRepository,
            ScheduleService scheduleService,
            NotificationsService notificationsService) {
        this.courseRepository = courseRepository;
        this.roomRepository = roomRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.examRepository = examRepository;
        this.scheduleService = scheduleService;
        this.userRepository = userRepository;
        this.notificationsService = notificationsService;
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
        savedExam.getAllStudents().forEach(student -> {
            notificationsService.sendNotification("Exam for course " + savedExam.getCourse().getName()
                    + " has been set on " + savedExam.getExamDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    + " at " + savedExam.getTimeSlot().getStartTime() + " in " + savedExam.getRoom().getName()
                    + " room.",
                    student.getId());
        });
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
    public List<ExamDto> getAllExamsForUserWithId(int userId) {
        UserEntity forUser = userRepository.findById(userId)
                .orElseThrow(() -> new InternalErrorException("Something went wrong!"));
        if ("ROLE_PROFESOR".equals(forUser.getRole()))
            return examRepository.findAllByProfessorId(userId).stream()
                    .map(exam -> mapper.examToExamDto(exam)).collect(Collectors.toList());
        if ("ROLE_STUDENT".equals(forUser.getRole()))
            return examRepository.findAllByAllStudents_Id(userId).stream()
                    .map(exam -> {
                        ExamDto examDto = mapper.examToExamDto(exam);
                        if (examDto.getEnrolledStudents().contains(Mappers.userEntityToDto(forUser))) {
                            examDto.setEnrolled(true);
                        } else {
                            examDto.setEnrolled(false);
                        }
                        return examDto;
                    }).collect(Collectors.toList());
        return new ArrayList<>();
    }

    @Override
    public ExamDto getExamById(int examId) {
        return mapper.examToExamDto(examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam with ID:" + examId + " does not exist")));
    }

    @Transactional
    @Override
    public ResponseDto enrollStudentToExam(int userId, int examId) {
        ResponseDto responseDto = new ResponseDto();
        UserEntity enrollStudent = userRepository.findById(userId)
                .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong"));

        exam.getEnrolledStudents().add(enrollStudent);
        Exam examSaved = examRepository.save(exam);
        responseDto.setStatus("success");
        responseDto.setMessage("Successfully enrolled to " + exam.getCourse().getName() + " exam.");
        return responseDto;
    }

    @Override
    public boolean amIEnrolled(int userId, int examId) {
        return examRepository.isStudentEnrolledInExam(userId, examId);
    }

}
