package com.example.my_campus_core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
import com.example.my_campus_core.service.impl.CourseServiceImpl;
import com.example.my_campus_core.util.Mappers;
import com.example.my_campus_core.service.impl.ScheduleServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CourseServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService; // oder dein konkreter Servicename

    @Mock
    private ScheduleService scheduleService; // korrekt als Mock!

    private CourseDto courseDto;
    private UserEntity professor;
    private UserEntity student;

    @BeforeEach
    void setUp() {
        courseDto = new CourseDto();
        courseDto.setName("Mathematik");
        courseDto.setDescription("Algebra und Analysis");
        courseDto.setProfessorId(1);
        List<Integer> studentIds = new ArrayList<>();
        studentIds.add(2);
        courseDto.setStudentsIds(studentIds);

        professor = new UserEntity();
        professor.setId(1);
        professor.setFirstName("Prof");
        professor.setLastName("Müller");

        student = new UserEntity();
        student.setId(2);
        student.setFirstName("Max");
        student.setLastName("Mustermann");
        student.setStatus("Pending"); // damit es in "Active" gesetzt wird
    }

    @Test
    void addCourse_WithValidData_SavesCourse() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.of(professor));
    when(userRepository.findById(2)).thenReturn(Optional.of(student));

    // Act
    courseService.addCourse(courseDto);

    // Assert
    assertEquals("Active", student.getStatus()); // Status wurde geändert
    verify(userRepository, times(1)).saveAndFlush(student); // wurde gespeichert
    verify(courseRepository, times(1)).save(any(Course.class)); // Kurs wurde gespeichert
}

@Test
void getAllCourses_WithOneCourse_ReturnsCourseDtoList() {
    // Arrange
    UserEntity professor = new UserEntity();
    professor.setId(1);
    professor.setFirstName("Alice");
    professor.setLastName("Smith");

    UserEntity student = new UserEntity();
    student.setId(2);
    student.setFirstName("Bob");
    student.setLastName("Jones");

    Course course = new Course();
    course.setId(100);
    course.setName("Mathematik");
    course.setDescription("Grundlagen");
    course.setProfessor(professor);
    List<UserEntity> students = new ArrayList<>();
    students.add(student);
    course.setStudents(students);

    List<Course> courseList = List.of(course);
    Page<Course> coursePage = new PageImpl<>(courseList);

    when(courseRepository.findAll(any(Pageable.class))).thenReturn(coursePage);
    when(userRepository.findById(2)).thenReturn(Optional.of(student)); // Für getStudentsInCourse()

    // Act
    List<CourseDto> result = courseService.getAllCourses(0);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    CourseDto dto = result.get(0);
    assertEquals("Mathematik", dto.getName());
    assertEquals("Grundlagen", dto.getDescription());
    assertEquals(1, dto.getProfessor().getId());
    assertEquals(1, dto.getStudents().size());
    assertEquals(2, dto.getStudents().get(0).getId());

    verify(courseRepository, times(1)).findAll(any(Pageable.class));
}

@Test
void getStudentsInCourse_WithValidIds_ReturnsUserDtos() {
    // Arrange
    int studentId = 2;

    UserEntity student = new UserEntity();
    student.setId(studentId);
    student.setFirstName("Max");
    student.setLastName("Mustermann");

    List<Integer> studentIds = List.of(studentId);

    when(userRepository.findById(studentId)).thenReturn(Optional.of(student));

    // Act
    List<UserDto> result = courseService.getStudentsInCourse(studentIds);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(studentId, result.get(0).getId());
    assertEquals("Max", result.get(0).getFirstName());
    assertEquals("Mustermann", result.get(0).getLastName());

    verify(userRepository, times(1)).findById(studentId);
}

@Test
void getCourseById_WithValidId_ReturnsCourseDto() {
    // Arrange
    int courseId = 10;

    UserEntity professor = new UserEntity();
    professor.setId(1);
    professor.setFirstName("Alice");
    professor.setLastName("Smith");

    UserEntity student = new UserEntity();
    student.setId(2);
    student.setFirstName("Bob");
    student.setLastName("Jones");

    Course course = new Course();
    course.setId(courseId);
    course.setName("Mathematik");
    course.setDescription("Algebra");
    course.setProfessor(professor);
    course.setStudents(List.of(student));

    when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
    when(userRepository.findById(2)).thenReturn(Optional.of(student)); // genutzt in getStudentsInCourse()

    // Act
    CourseDto result = courseService.getCourseById(courseId);

    // Assert
    assertNotNull(result);
    assertEquals(courseId, result.getId());
    assertEquals("Mathematik", result.getName());
    assertEquals("Algebra", result.getDescription());
    assertEquals(1, result.getProfessor().getId());
    assertEquals(1, result.getStudents().size());
    assertEquals(2, result.getStudents().get(0).getId());

    verify(courseRepository, times(1)).findById(courseId);
    verify(userRepository, times(1)).findById(2); // für getStudentsInCourse()
}

@Test
void getCourseById_WithInvalidId_ThrowsNotFoundException() {
    // Arrange
    int invalidId = 99;
    when(courseRepository.findById(invalidId)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException ex = assertThrows(NotFoundException.class,
        () -> courseService.getCourseById(invalidId));

    assertEquals("Course with ID 99 does not exist", ex.getMessage());
    verify(courseRepository, times(1)).findById(invalidId);
}

@Test
void getCoursesByUserId_AsStudent_ReturnsCourseDtos() {
    // Arrange
    int userId = 5;
    String userRole = "ROLE_STUDENT";

    UserEntity professor = new UserEntity();
    professor.setId(1);
    professor.setFirstName("Alice");

    Course course = new Course();
    course.setId(10);
    course.setName("Mathe");
    course.setDescription("Algebra");
    course.setProfessor(professor);

    when(courseRepository.findByStudentsId(userId)).thenReturn(List.of(course));

    // Act
    List<CourseDto> result = courseService.getCoursesByUserId(userId, userRole);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    CourseDto dto = result.get(0);
    assertEquals(10, dto.getId());
    assertEquals("Mathe", dto.getName());
    assertEquals("Algebra", dto.getDescription());
    assertEquals(1, dto.getProfessor().getId());

    verify(courseRepository, times(1)).findByStudentsId(userId);
    verify(courseRepository, times(0)).findByProfessorId(anyInt());
}

@Test
void getCoursesByUserId_AsProfessor_ReturnsCourseDtos() {
    // Arrange
    int userId = 3;
    String userRole = "ROLE_PROFESSOR";

    UserEntity professor = new UserEntity();
    professor.setId(userId);
    professor.setFirstName("Dr. John");

    Course course = new Course();
    course.setId(20);
    course.setName("Physik");
    course.setDescription("Mechanik");
    course.setProfessor(professor);

    when(courseRepository.findByProfessorId(userId)).thenReturn(List.of(course));

    // Act
    List<CourseDto> result = courseService.getCoursesByUserId(userId, userRole);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    CourseDto dto = result.get(0);
    assertEquals(20, dto.getId());
    assertEquals("Physik", dto.getName());
    assertEquals("Mechanik", dto.getDescription());
    assertEquals(userId, dto.getProfessor().getId());

    verify(courseRepository, times(1)).findByProfessorId(userId);
    verify(courseRepository, times(0)).findByStudentsId(anyInt());
}

@Test
void searchForCourses_WithMatchingName_ReturnsCourseDtos() {
    // Arrange
    String searchTerm = "mathe";

    UserEntity professor = new UserEntity();
    professor.setId(1);
    professor.setFirstName("Alice");

    UserEntity student = new UserEntity();
    student.setId(2);

    Course course = new Course();
    course.setId(10);
    course.setName("Mathematik");
    course.setDescription("Grundlagen");
    course.setProfessor(professor);
    course.setStudents(List.of(student));

    when(courseRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(List.of(course));
    when(userRepository.findById(2)).thenReturn(Optional.of(student)); // Für getStudentsInCourse

    // Act
    List<CourseDto> result = courseService.searchForCourses(searchTerm);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    CourseDto dto = result.get(0);
    assertEquals(10, dto.getId());
    assertEquals("Mathematik", dto.getName());
    assertEquals("Grundlagen", dto.getDescription());
    assertEquals(1, dto.getProfessor().getId());
    assertEquals(1, dto.getStudents().size());
    assertEquals(2, dto.getStudents().get(0).getId());

    verify(courseRepository, times(1)).findByNameContainingIgnoreCase(searchTerm);
    verify(userRepository, times(1)).findById(2);
}

@Test
void searchForCourses_NoMatch_ReturnsEmptyList() {
    // Arrange
    String searchTerm = "xyz";
    when(courseRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(new ArrayList<>());

    // Act
    List<CourseDto> result = courseService.searchForCourses(searchTerm);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(courseRepository, times(1)).findByNameContainingIgnoreCase(searchTerm);
    verifyNoMoreInteractions(courseRepository);
}

@Test
void updateCourse_WithValidInput_ReturnsSuccessResponse() {
    // Arrange
    int courseId = 1;
    int professorId = 2;
    int studentId1 = 3;
    int studentId2 = 4;

    CourseDto courseDto = new CourseDto();
    courseDto.setId(courseId);
    courseDto.setName("Neue Mathematik");
    courseDto.setDescription("Neue Beschreibung");
    courseDto.setProfessorId(professorId);
    courseDto.setStudentsIds(List.of(studentId1, studentId2));

    UserEntity professor = new UserEntity();
    professor.setId(professorId);

    UserEntity student1 = new UserEntity();
    student1.setId(studentId1);

    UserEntity student2 = new UserEntity();
    student2.setId(studentId2);

    Course existingCourse = new Course();
    existingCourse.setId(courseId);
    existingCourse.setStudents(new ArrayList<>());

    Course savedCourse = new Course();
    savedCourse.setId(courseId);
    savedCourse.setName("Neue Mathematik");
    savedCourse.setDescription("Neue Beschreibung");
    savedCourse.setProfessor(professor);
    savedCourse.setStudents(List.of(student1, student2));

    when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
    when(userRepository.findById(professorId)).thenReturn(Optional.of(professor));
    when(userRepository.findById(studentId1)).thenReturn(Optional.of(student1));
    when(userRepository.findById(studentId2)).thenReturn(Optional.of(student2));
    when(courseRepository.saveAndFlush(any(Course.class))).thenReturn(savedCourse);

    // Act
    ResponseDto response = courseService.updateCourse(courseDto);

    // Assert
    assertNotNull(response);
    assertEquals("success", response.getStatus());
    assertEquals("Course Neue Mathematik successfully updated.", response.getMessage());

    verify(courseRepository).findById(courseId);
    verify(userRepository).findById(professorId);
    verify(userRepository).findById(studentId1);
    verify(userRepository).findById(studentId2);
    verify(courseRepository).saveAndFlush(any(Course.class));
    verify(scheduleService).updateScheduleOnCourseUpdate(savedCourse);
}

@Test
void totalCourses_WithValidSize_ReturnsCorrectTotalPages() {
    // Arrange
    int totalCourses = 25;
    int pageSize = 10;
    long repoCount = totalCourses;

    when(courseRepository.count()).thenReturn(repoCount);

    // Act
    int result = courseService.totalCourses(pageSize);

    // Assert
    assertEquals(3, result); // 25 Kurse / 10 pro Seite => 3 Seiten (2 voll, 1 teilvoll)
    verify(courseRepository, times(1)).count();
}

}

