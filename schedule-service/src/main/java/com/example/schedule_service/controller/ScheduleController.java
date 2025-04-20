package com.example.schedule_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.schedule_service.dto.request.RequestDto;
import com.example.schedule_service.models.ScheduleSolution;
import com.example.schedule_service.service.ScheduleService;

@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController {
    private ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/generate")
    public ResponseEntity generateSchedule(@RequestBody RequestDto requestDto) {

        // System.out.println(requestDto);
        ScheduleSolution response = scheduleService.generate(requestDto);
        System.out.println("Hellp" + response);
        return new ResponseEntity<>("Recived" + response, HttpStatus.OK);

    }
}