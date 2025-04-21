package com.example.my_campus_core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;
    private UserRepository userRepository;

    public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addCourse(CourseDto courseDto) {
        Course course = new Course();
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());
        course.setProfessor(userRepository.findById(courseDto.getProfessorId()).orElse(null));
        List<UserEntity> students = new ArrayList<>();
        for (int studentId : courseDto.getStudentsIds()) {
            System.out.println("Student ID: " + studentId);
            UserEntity student = userRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("User with ID " + studentId + " does not exist"));
            if (student.getStatus() == "Pending") {
                student.setStatus("Active");
                userRepository.saveAndFlush(student);
            }
            students.add(student);
        }
        course.setStudents(students);
        courseRepository.save(course);
    }

    @Override
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        List<CourseDto> courseDtos = new ArrayList<>();

        for (Course course : courses) {
            CourseDto courseDto = new CourseDto();
            courseDto.setId(course.getId());
            courseDto.setName(course.getName());
            courseDto.setDescription(course.getDescription());
            courseDto.setProfessor(mapUserToUserDto(course.getProfessor()));
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
                    .orElseThrow(() -> new IllegalArgumentException("User with ID " + studentId + " does not exist"));
            if (student != null) {
                students.add(mapUserToUserDto(student));
            }
        }
        return students;
    }

    public UserDto mapUserToUserDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setStatus(user.getStatus());
        return userDto;
    }

    @Override
    public CourseDto getCourseById(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + courseId + " does not exist"));
        List<Integer> studentIds = course.getStudents().stream()
                .map(UserEntity::getId)
                .toList();
        List<UserDto> students = getStudentsInCourse(studentIds);
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setDescription(course.getDescription());
        courseDto.setProfessor(mapUserToUserDto(course.getProfessor()));
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
            courseDto.setProfessor(mapUserToUserDto(course.getProfessor()));
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
                courseDto.setProfessor(mapUserToUserDto(course.getProfessor()));
                List<Integer> studentIds = course.getStudents().stream()
                        .map(UserEntity::getId)
                        .toList();
                courseDto.setStudents(getStudentsInCourse(studentIds));
                courseDtos.add(courseDto);
            }
        }
        return courseDtos;
    }
}