package com.example.my_campus_core.service.impl;

import java.util.ArrayList;
import java.util.List;

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
import com.example.my_campus_core.service.ScheduleService;
import com.example.my_campus_core.util.Mappers;

@Service
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private ScheduleService scheduleService;
    private Mappers mappers;

    public CourseServiceImpl(ScheduleService scheduleService, CourseRepository courseRepository,
            UserRepository userRepository, Mappers mappers) {
        this.scheduleService = scheduleService;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.mappers = mappers;
    }

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
        courseRepository.save(course);
    }

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

        } else {
            responseDto.setStatus("error");
            responseDto.setMessage("Error. Course " + courseDto.getName() + " was not updated.");
        }

        return responseDto;
    }

    @Override
    public int totalCourses(int size) {
        int totalCourses = (int) courseRepository.count();
        int totalPages = (int) Math.ceil((double) totalCourses / size);

        return totalPages;
    }
}