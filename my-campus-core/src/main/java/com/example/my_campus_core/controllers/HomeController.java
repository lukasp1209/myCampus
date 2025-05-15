package com.example.my_campus_core.controllers;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.ExamDto;
import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.service.CourseService;
import com.example.my_campus_core.service.ExamService;
import com.example.my_campus_core.service.ScheduleService;
import com.example.my_campus_core.service.UserService;
import com.example.my_campus_core.security.CustomUserDetailsService;

import com.example.my_campus_core.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ExamService examService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @GetMapping("/")
    public String getHomePage(Model model) {
        int userId = customUserDetailsService.getUserIdByEmail(SecurityUtil.getSessionUser());

        model.addAttribute("courses",
                courseService.getCoursesByUserId(
                        userId,
                        customUserDetailsService.getUserRoleByEmail(SecurityUtil.getSessionUser())));

        return "./home";
    }

}
