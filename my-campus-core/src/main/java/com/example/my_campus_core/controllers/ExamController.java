package com.example.my_campus_core.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ExamController {
    @GetMapping("/exams")
    public String getExamsPage(Model model) {
        return "./exams";
    }

    @GetMapping("/exam/create/{courseId}")
    public String getCreateExamPage(Model model, @PathVariable int courseId) {
        return "./examCreate";
    }

}
