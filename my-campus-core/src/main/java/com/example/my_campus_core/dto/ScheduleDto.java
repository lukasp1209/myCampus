package com.example.my_campus_core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.my_campus_core.models.Lecture;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.TimeSlot;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
public class ScheduleDto {
    private int id;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private List<LectureDto> lectureList = new ArrayList<>();
    private List<ExamDto> examList = new ArrayList<>();
    private List<RoomDto> roomList = new ArrayList<>();
    private List<TimeSlot> timeSlotList = new ArrayList<>();

}
