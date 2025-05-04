package com.example.my_campus_core.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.service.ScheduleService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LectureController {

    private ScheduleService scheduleService;

    public LectureController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/lecture/{lectureId}")
    public String getLecturePage(@PathVariable int lectureId, Model model) {
        model.addAttribute("lecture", scheduleService.getLectureById(lectureId));
        model.addAttribute("markAttendance", scheduleService.isLectureNow(lectureId));
        return "./lecture";
    }

}
