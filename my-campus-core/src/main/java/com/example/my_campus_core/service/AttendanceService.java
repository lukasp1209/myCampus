package com.example.my_campus_core.service;

import com.example.my_campus_core.dto.AttendanceDto;
import com.example.my_campus_core.dto.response.ResponseDto;

public interface AttendanceService {
    AttendanceDto amIMarkedAsAttending(int lectureId, int userId);

    ResponseDto setAsAttending(int lectureId, int userId);
}
