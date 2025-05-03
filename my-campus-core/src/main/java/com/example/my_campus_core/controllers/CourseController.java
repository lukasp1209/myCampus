package com.example.my_campus_core.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.ExamDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.security.CustomUserDetailsService;
import com.example.my_campus_core.security.SecurityUtil;
import com.example.my_campus_core.service.CourseService;
import com.example.my_campus_core.service.ExamService;
import com.example.my_campus_core.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CourseController {
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private CourseService courseService;
    private CustomUserDetailsService customUserDetailsService;
    private UserService userService;
    private ExamService examService;

    @Autowired
    public CourseController(
            CourseService courseService,
            UserService userService,
            CustomUserDetailsService customUserDetailsService,
            ExamService examService) {

        this.courseService = courseService;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.examService = examService;
    }

    @GetMapping("/course/managment")
    public String getCourseManagmentPage(@RequestParam(defaultValue = "0") int page, Model model) {
        logger.info("Accessing course management page with page number: {}", page);
        model.addAttribute("courses", courseService.getAllCourses(page));
        model.addAttribute("page", page);
        int totalPages = courseService.totalCourses(10);
        if (totalPages == 0) {
            logger.debug("No courses found, setting total pages to 1");
            totalPages++;
        }
        model.addAttribute("totalPages", totalPages);
        logger.debug("Total pages calculated: {}", totalPages);
        return "./courseManagment";
    }

    @GetMapping("/course/{courseId}")
    public String getCoursePage(@PathVariable int courseId, Model model) {
        logger.info("Accessing course page for course ID: {}", courseId);
        CourseDto course = courseService.getCourseById(courseId);
        if (course == null) {
            logger.warn("Course not found for ID: {}", courseId);
            return "redirect:/course/managment";
        }
        model.addAttribute("course", course);
        
        boolean hasExam = examService.examForCourseExists(courseId);
        logger.debug("Exam exists for course {}: {}", courseId, hasExam);
        model.addAttribute("setExam", !hasExam);
        
        ExamDto exam = examService.getExamCourseId(courseId);
        logger.debug("Retrieved exam for course {}: {}", courseId, exam);
        model.addAttribute("exam", exam);
        
        return "./course";
    }

    @GetMapping("/courses")
    public String getCoursesPage(Model model) {
        logger.info("Courses page /courses accessed!");
        model.addAttribute("courses",
                courseService
                        .getCoursesByUserId(customUserDetailsService.getUserIdByEmail(SecurityUtil.getSessionUser()),
                                customUserDetailsService.getUserRoleByEmail(SecurityUtil.getSessionUser())));

        return "./courses";
    }

    @GetMapping("/course/edit/{courseId}")
    public String getCourseEditPage(@PathVariable int courseId, Model model) {
        model.addAttribute("course", courseService.getCourseById(courseId));
        System.out.println("Students: " + courseService.getCourseById(courseId).getStudents());
        return "./editCourse";
    }

    @PostMapping("/course/edit/{courseId}")
    public String updateCourse(@PathVariable int courseId, @ModelAttribute CourseDto courseDto,
            RedirectAttributes redirectAttributes) {

        ResponseDto response = courseService.updateCourse(courseDto);
        redirectAttributes.addFlashAttribute("response", response);
        return "redirect:/course/managment";
    }

    @GetMapping("/course/add")
    public String getAddCoursePage() {
        return "./addCourse"; // Return the name of the add course view (e.g., addCourse.html)
    }

    @PostMapping("/course/add")
    public String addCourse(@ModelAttribute CourseDto courseDto) {
        courseService.addCourse(courseDto); // Call the service to add the course
        System.out.println(courseDto);

        return "redirect:/course/managment"; // Redirect to the course management page after adding a course
    }

}
