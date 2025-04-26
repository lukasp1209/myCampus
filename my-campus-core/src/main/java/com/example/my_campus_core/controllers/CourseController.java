package com.example.my_campus_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.my_campus_core.dto.CourseDto;
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
        model.addAttribute("courses", courseService.getAllCourses(page)); // Add the list of courses to the model
        model.addAttribute("page", page);
        int totalPages = courseService.totalCourses(10);
        if (totalPages == 0)
            totalPages++;
        model.addAttribute("totalPages", totalPages);
        System.out.println("Total Pages " + courseService.totalCourses(10));
        return "./courseManagment"; // Return the name of the course management view (e.g., courseManagment.html)
    }

    @GetMapping("/course/{courseId}")
    public String getCoursePage(@PathVariable int courseId, Model model) {
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("setExam", !examService.examForCourseExists(courseId));
        model.addAttribute("exam", examService.getExamCourseId(courseId));
        System.out.println(examService.getExamCourseId(courseId));
        return "./course"; // Return the name of the course view (e.g., course.html)
    }

    @GetMapping("/courses")
    public String getCoursesPage(Model model) {
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
