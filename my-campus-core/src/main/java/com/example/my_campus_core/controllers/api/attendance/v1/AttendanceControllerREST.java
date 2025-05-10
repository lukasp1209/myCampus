package com.example.my_campus_core.controllers.api.attendance.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.example.my_campus_core.security.CustomUserDetailsService;
import com.example.my_campus_core.service.AttendanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceControllerREST {
    private AttendanceService attendanceService;
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public AttendanceControllerREST(AttendanceService attendanceService,
            CustomUserDetailsService customUserDetailsService) {
        this.attendanceService = attendanceService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity getAttendance(@PathVariable int lectureId, @RequestParam String userEmail) {
        return new ResponseEntity<>(
                attendanceService.amIMarkedAsAttending(lectureId, customUserDetailsService.getUserIdByEmail(userEmail)),
                HttpStatus.OK);
    }

}
