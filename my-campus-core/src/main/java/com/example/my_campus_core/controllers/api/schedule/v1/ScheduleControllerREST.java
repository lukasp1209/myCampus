package com.example.my_campus_core.controllers.api.schedule.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_campus_core.service.ScheduleService;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleControllerREST {

    private ScheduleService scheduleService;

    public ScheduleControllerREST(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/rooms")
    public ResponseEntity getMethodName(@RequestParam List<Integer> ignoreRoomIds) {

        return new ResponseEntity<>(scheduleService.getAllRoomsIgnoreRooms(ignoreRoomIds), null, 200);
    }

}
