package com.example.my_campus_core.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class ScheduleRequestDto {
    private List<Integer> courseIds;
    private List<Integer> roomIds;
    private int weekOffset;
}
