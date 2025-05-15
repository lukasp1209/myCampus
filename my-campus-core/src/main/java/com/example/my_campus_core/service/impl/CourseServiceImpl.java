package com.example.my_campus_core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.exceptions.InputMissingException;
import com.example.my_campus_core.exceptions.NotFoundException;
import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.CourseService;
import com.example.my_campus_core.service.NotificationsService;
import com.example.my_campus_core.service.ScheduleService;
import com.example.my_campus_core.util.Mappers;

/**
 * Implementation of the CourseService interface that handles course-related operations.
 * This service manages course creation, updates, and retrieval of course information.
 */
@Service
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private ScheduleService scheduleService;
    private NotificationsService notificationsService;
    private Mappers mappers;

    /**
     * Constructs a new CourseServiceImpl with required dependencies.
     *
     * @param scheduleService Service for schedule-related operations
     * @param courseRepository Repository for course-related operations
     * @param userRepository Repository for user-related operations
     * @param mappers Utility for mapping between entities and DTOs
     * @param notificationsService Service for notification-related operations
     */
    public CourseServiceImpl(ScheduleService scheduleService, CourseRepository courseRepository,
            UserRepository userRepository, Mappers mappers, NotificationsService notificationsService) {
        this.scheduleService = scheduleService;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.mappers = mappers;
        this.notificationsService = notificationsService;
    }

    /**
     * Adds a new course to the system.
     * Notifies the professor and enrolled students about the course creation.
     *
     * @param courseDto The course data transfer object containing course information
     * @throws InputMissingException if the professor is not provided
     * @throws NotFoundException if a student is not found
     */
    @Override
    public void addCourse(CourseDto courseDto) {
        Course course = new Course();
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());
        course.setProfessor(userRepository.findById(courseDto.getProfessorId()).orElseThrow(
                () -> new InputMissingException("Professor for course " + courseDto.getName() + " is not provided.")));
        List<UserEntity> students = new ArrayList<>();
        if (courseDto.getStudentsIds() != null) {
            for (int studentId : courseDto.getStudentsIds()) {
                System.out.println("Student ID: " + studentId);
                UserEntity student = userRepository.findById(studentId)
                        .orElseThrow(
                                () -> new NotFoundException("User with ID: " + studentId + " not found"));
                if (student.getStatus() == "Pending") {
                    student.setStatus("Active");
                    userRepository.saveAndFlush(student);
                }
                students.add(student);
            }
        }
        course.setStudents(students);
        Course newCourse = courseRepository.save(course);
        String messageProfessor = "You have been assigned to teach: " + newCourse.getName() + ".";
        notificationsService.sendNotification(messageProfessor, newCourse.getProfessor().getId());
        if (newCourse.getStudents().isEmpty())
            return;
        newCourse.getStudents().forEach(student -> {
            String messageStudent = "You have been enrolled in: " + newCourse.getName() +
                    ", taught by Professor " + newCourse.getProfessor().getFirstName() +
                    " " + newCourse.getProfessor().getLastName() + ".";
            notificationsService.sendNotification(messageStudent, student.getId());
        });

    }

    /**
     * Retrieves a paginated list of all courses.
     *
     * @param page The page number (0-based)
     * @return List of CourseDto objects containing course information
     */
    @Override
    public List<CourseDto> getAllCourses(int page) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Course> courses = courseRepository.findAll(pageable);
        List<CourseDto> courseDtos = new ArrayList<>();
        System.out.println("ABC" + courses);
        for (Course course : courses) {
            CourseDto courseDto = new CourseDto();
            System.out.println("TBC " + course);
            courseDto.setId(course.getId());
            courseDto.setName(course.getName());
            courseDto.setDescription(course.getDescription());
            courseDto.setProfessor(mappers.userEntityToDto(course.getProfessor()));
            List<Integer> studentIds = course.getStudents().stream()
                    .map(UserEntity::getId)
                    .toList();
            courseDto.setStudents(getStudentsInCourse(studentIds));
            courseDtos.add(courseDto);
        }
        return courseDtos;
    }

    /**
     * Retrieves a list of students enrolled in a course.
     *
     * @param studentIds List of student IDs to retrieve
     * @return List of UserDto objects containing student information
     * @throws NotFoundException if a student is not found
     */
    public List<UserDto> getStudentsInCourse(List<Integer> studentIds) {
        List<UserDto> students = new ArrayList<>();

        for (int studentId : studentIds) {
            UserEntity student = userRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("User with ID " + studentId + " does not exist"));
            if (student != null) {
                students.add(mappers.userEntityToDto(student));
            }
        }
        return students;
    }

    /**
     * Retrieves a course by its ID.
     *
     * @param courseId The ID of the course to retrieve
     * @return CourseDto containing the course information
     * @throws NotFoundException if the course does not exist
     */
    @Override
    public CourseDto getCourseById(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with ID " + courseId + " does not exist"));
        List<Integer> studentIds = course.getStudents().stream()
                .map(UserEntity::getId)
                .toList();
        List<UserDto> students = getStudentsInCourse(studentIds);

        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setDescription(course.getDescription());
        courseDto.setProfessor(mappers.userEntityToDto(course.getProfessor()));
        courseDto.setStudents(students);
        return courseDto;
    }

    /**
     * Retrieves courses associated with a specific user based on their role.
     *
     * @param userId The ID of the user
     * @param userRole The role of the user (ROLE_STUDENT or ROLE_PROFESSOR)
     * @return List of CourseDto objects containing course information
     */
    @Override
    public List<CourseDto> getCoursesByUserId(int userId, String userRole) {
        List<Course> courses = "ROLE_STUDENT".equals(userRole)
                ? courseRepository.findByStudentsId(userId)
                : courseRepository.findByProfessorId(userId);
        List<CourseDto> courseDtos = new ArrayList<>();
        for (Course course : courses) {
            CourseDto courseDto = new CourseDto();
            courseDto.setId(course.getId());
            courseDto.setName(course.getName());
            courseDto.setDescription(course.getDescription());
            courseDto.setProfessor(mappers.userEntityToDto(course.getProfessor()));
            courseDtos.add(courseDto);
        }
        return courseDtos;
    }

    /**
     * Searches for courses by name.
     *
     * @param searchTerm The term to search for in course names
     * @return List of CourseDto objects matching the search term
     */
    @Override
    public List<CourseDto> searchForCourses(String searchTerm) {
        List<Course> courses = courseRepository.findByNameContainingIgnoreCase(searchTerm);
        if (courses.isEmpty()) {
            return new ArrayList<>();
        }
        List<CourseDto> courseDtos = new ArrayList<>();

        for (Course course : courses) {
            if (course.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                CourseDto courseDto = new CourseDto();
                courseDto.setId(course.getId());
                courseDto.setName(course.getName());
                courseDto.setDescription(course.getDescription());
                courseDto.setProfessor(mappers.userEntityToDto(course.getProfessor()));
                List<Integer> studentIds = course.getStudents().stream()
                        .map(UserEntity::getId)
                        .toList();
                courseDto.setStudents(getStudentsInCourse(studentIds));
                courseDtos.add(courseDto);
            }
        }
        return courseDtos;
    }

    /**
     * Updates an existing course.
     * Notifies affected professors and students about the changes.
     *
     * @param courseDto The course data transfer object containing updated information
     * @return ResponseDto indicating the result of the update operation
     * @throws NotFoundException if the course or any referenced user is not found
     */
    @Override
    public ResponseDto updateCourse(CourseDto courseDto) {
        ResponseDto responseDto = new ResponseDto();
        Course course = courseRepository.findById(courseDto.getId())
                .orElseThrow(() -> new NotFoundException("Course with ID:" + courseDto.getId() + " can't be updated"));

        if (course != null) {

            course.setName(courseDto.getName());
            course.setProfessor(userRepository.findById(courseDto.getProfessorId())
                    .orElseThrow(() -> new NotFoundException("Professor with ID" + courseDto.getProfessorId()
                            + "can't be found. Please reach out to platform support")));
            List<UserEntity> updatedStudents = new ArrayList<>();
            for (int student : courseDto.getStudentsIds()) {
                UserEntity updatedStudent = userRepository.findById(student)
                        .orElseThrow(() -> new NotFoundException("Student with ID" + student
                                + "can't be found. Please reach out to platform support"));
                updatedStudents.add(updatedStudent);
            }
            course.setStudents(updatedStudents);
            course.setDescription(courseDto.getDescription());
            Course updatedCourse = courseRepository.saveAndFlush(course);
            if (updatedCourse.getStudents().equals(course.getStudents()))
                scheduleService.updateScheduleOnCourseUpdate(updatedCourse);
            responseDto.setStatus("success");
            responseDto.setMessage("Course " + course.getName() + " successfully updated.");
            updateCourseNotifications(course, updatedCourse);

        } else {
            responseDto.setStatus("error");
            responseDto.setMessage("Error. Course " + courseDto.getName() + " was not updated.");
        }

        return responseDto;
    }

    /**
     * Calculates the total number of pages for course pagination.
     *
     * @param size The number of items per page
     * @return The total number of pages
     */
    @Override
    public int totalCourses(int size) {
        int totalCourses = (int) courseRepository.count();
        int totalPages = (int) Math.ceil((double) totalCourses / size);

        return totalPages;
    }

    /**
     * Sends notifications to professors and students about course updates.
     *
     * @param course The original course
     * @param updatedCourse The updated course
     */
    public void updateCourseNotifications(Course course, Course updatedCourse) {
        if (course.getProfessor().equals(updatedCourse.getProfessor())) {
            notificationsService.sendNotification("Course " + updatedCourse.getName() + " has been updated",
                    updatedCourse.getProfessor().getId());
        } else {
            notificationsService.sendNotification(
                    "Removed as professor from: " + course.getName(),
                    course.getProfessor().getId());

            notificationsService.sendNotification(
                    "Assigned as professor to: " + updatedCourse.getName(),
                    updatedCourse.getProfessor().getId());
        }

        if (course.getStudents().equals(updatedCourse.getStudents())) {
            updatedCourse.getStudents().forEach(student -> {
                notificationsService.sendNotification("Course " + updatedCourse.getName() + " has been updated",
                        student.getId());
            });
        } else {
            List<UserEntity> newStudents = updatedCourse.getStudents().stream()
                    .filter(student -> !course.getStudents().contains(student))
                    .collect(Collectors.toList());
            List<UserEntity> removedStudents = course.getStudents().stream()
                    .filter(student -> !updatedCourse.getStudents().contains(student))
                    .collect(Collectors.toList());

            List<UserEntity> oldStudents = course.getStudents().stream()
                    .filter(student -> updatedCourse.getStudents().contains(student))
                    .collect(Collectors.toList());
            if (!oldStudents.isEmpty()) {
                oldStudents.forEach(student -> {
                    notificationsService.sendNotification("Course " + updatedCourse.getName() + "has been updated",
                            student.getId());
                });
            }
            if (!newStudents.isEmpty()) {
                newStudents.forEach(student -> {
                    notificationsService.sendNotification("You have been enrolled in: " + updatedCourse.getName() +
                            ", taught by Professor " + updatedCourse.getProfessor().getFirstName() +
                            " " + updatedCourse.getProfessor().getLastName() + ".",
                            student.getId());
                });
            }
            if (!removedStudents.isEmpty()) {
                removedStudents.forEach(student -> {
                    notificationsService.sendNotification("You have been removed from: " + updatedCourse.getName() +
                            ", taught by Professor " + updatedCourse.getProfessor().getFirstName() +
                            " " + updatedCourse.getProfessor().getLastName() + ".",
                            student.getId());
                });
            }
        }
    }
}