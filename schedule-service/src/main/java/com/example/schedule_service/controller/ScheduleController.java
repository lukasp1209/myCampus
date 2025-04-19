package com.example.schedule_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.schedule_service.dto.request.RequestDto;

@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    @PostMapping("/generate")
    public ResponseEntity generateSchedule(@RequestBody RequestDto requestDto) {

        System.out.println(requestDto);
        // Logic to generate schedule based on the request
        return new ResponseEntity<>("Recived" + requestDto, HttpStatus.OK);

    }
}