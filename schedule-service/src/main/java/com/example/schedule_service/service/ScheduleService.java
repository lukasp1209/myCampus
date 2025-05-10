package com.example.schedule_service.service;

import com.example.schedule_service.dto.request.RequestDto;
import com.example.schedule_service.models.ScheduleSolution;

public interface ScheduleService {
    ScheduleSolution generate(RequestDto requestDto);
}
