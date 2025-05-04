package com.example.my_campus_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.my_campus_core.service.AttendanceService;

@Controller
public class AttendanceController {
    private AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/attendance/{lectureId}")
    public String postAttendance(@PathVariable int lectureId, @RequestParam int userId,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("response", attendanceService.setAsAttending(lectureId, userId));
        return "redirect:/lecture/" + lectureId;
    }
}
