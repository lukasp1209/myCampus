package com.example.my_campus_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.my_campus_core.dto.request.ExamRequestDto;
import com.example.my_campus_core.service.ExamService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ExamController {
    private ExamService examService;

    @Autowired
    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/exams")
    public String getExamsPage(Model model) {
        return "./exams";
    }

    @GetMapping("/exam/create/{courseId}")
    public String getCreateExamPage(Model model, @PathVariable int courseId) {
        model.addAttribute("courseId", courseId);
        return "./examCreate";
    }

    @PostMapping("/exam/create")
    public String postMethodName(@ModelAttribute ExamRequestDto examRequestDto) {
        System.out.println(examRequestDto);
        examService.createNewExam(examRequestDto);
        return "redirect:/exams";
    }

}
