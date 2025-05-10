package com.example.my_campus_core.dto.response;

import java.util.List;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.LectureDto;
import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.models.TimeSlot;

import lombok.Data;

@Data
public class ScheduleSolutionResponseDto {
    private List<TimeSlot> timeSlotList;
    private List<RoomDto> roomList;
    private List<CourseDto> courseList;
    private List<UserDto> professorList;
    private List<LectureDto> lectureList;
    private String score;
}
