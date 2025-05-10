package com.example.my_campus_core.dto.request;

import lombok.Data;

@Data
public class ExamRequestDto {
    private int courseId;
    private String examDate;
    private int examTime;
    private int roomId;
}
