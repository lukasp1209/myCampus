package com.example.my_campus_core.dto;

import lombok.Data;

@Data
public class AttendanceDto {
    private int userId;
    private int lectureId;
    private boolean isLectureNow;
    private boolean isAttending;

}
