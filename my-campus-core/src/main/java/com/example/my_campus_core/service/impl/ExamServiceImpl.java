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

/**
 * Implementation of the ExamService interface that handles exam-related operations.
 * This service manages exam creation, enrollment, and retrieval of exam information.
 */
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

    /**
     * Constructs a new ExamServiceImpl with all required dependencies.
     *
     * @param courseRepository Repository for course-related operations
     * @param userRepository Repository for user-related operations
     * @param roomRepository Repository for room-related operations
     * @param timeSlotRepository Repository for time slot-related operations
     * @param examRepository Repository for exam-related operations
     * @param scheduleService Service for schedule-related operations
     * @param notificationsService Service for notification-related operations
     */
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

    /**
     * Creates a new exam based on the provided exam request data.
     * Also schedules the exam and notifies all enrolled students.
     *
     * @param examRequestDto The exam request data containing exam details
     * @return ResponseDto indicating the result of the operation
     */
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

    /**
     * Checks if an exam exists for a given course.
     *
     * @param courseId The ID of the course to check
     * @return true if an exam exists for the course, false otherwise
     */
    @Override
    public boolean examForCourseExists(int courseId) {
        return examRepository.existsByCourseId(courseId);
    }

    /**
     * Retrieves exam information for a specific course.
     *
     * @param courseId The ID of the course
     * @return ExamDto containing the exam information, or empty ExamDto if no exam exists
     */
    @Override
    public ExamDto getExamCourseId(int courseId) {
        if (examForCourseExists(courseId)) {
            ExamDto examDto = mapper.examToExamDto(examRepository.findByCourseId(courseId));
            return examDto;
        }
        return new ExamDto();
    }

    /**
     * Retrieves all exams for a specific user based on their role.
     * For professors, returns exams they are teaching.
     * For students, returns exams they are enrolled in.
     *
     * @param userId The ID of the user
     * @return List of ExamDto objects containing exam information
     */
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

    /**
     * Retrieves exam information by its ID.
     *
     * @param examId The ID of the exam to retrieve
     * @return ExamDto containing the exam information
     * @throws NotFoundException if the exam does not exist
     */
    @Override
    public ExamDto getExamById(int examId) {
        return mapper.examToExamDto(examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Exam with ID:" + examId + " does not exist")));
    }

    /**
     * Enrolls a student in an exam.
     *
     * @param userId The ID of the student to enroll
     * @param examId The ID of the exam to enroll in
     * @return ResponseDto indicating the result of the enrollment operation
     */
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

    /**
     * Checks if a student is enrolled in a specific exam.
     *
     * @param userId The ID of the student to check
     * @param examId The ID of the exam to check
     * @return true if the student is enrolled, false otherwise
     */
    @Override
    public boolean amIEnrolled(int userId, int examId) {
        return examRepository.isStudentEnrolledInExam(userId, examId);
    }

}
