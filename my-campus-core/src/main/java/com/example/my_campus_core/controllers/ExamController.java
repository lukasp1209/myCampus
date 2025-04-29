package com.example.my_campus_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.dto.request.ExamRequestDto;
import com.example.my_campus_core.security.SecurityUtil;
import com.example.my_campus_core.service.ExamService;
import com.example.my_campus_core.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ExamController {
    private ExamService examService;
    private SecurityUtil securityUtil;
    private UserService userService;

    @Autowired
    public ExamController(ExamService examService, UserService userService) {
        this.examService = examService;
        this.userService = userService;
    }

    @GetMapping("/exams")
    public String getExamsPage(Model model) {
        model.addAttribute("exams", examService
                .getAllExamsForUserWithId(userService.getUserByEmail(securityUtil.getSessionUser()).getId()));
        return "./exams";
    }

    @GetMapping("/exam/create/{courseId}")
    public String getCreateExamPage(Model model, @PathVariable int courseId) {
        model.addAttribute("courseId", courseId);
        return "./examCreate";
    }

    @GetMapping("/exam/{examId}")
    public String getExamPage(Model model, @PathVariable int examId) {
        model.addAttribute("exam", examService.getExamById(examId));
        return "./exam";
    }

    @PostMapping("/exam/create")
    public String createNewExam(@ModelAttribute ExamRequestDto examRequestDto) {
        System.out.println(examRequestDto);
        examService.createNewExam(examRequestDto);
        return "redirect:/exams";
    }

}
