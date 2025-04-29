package com.example.my_campus_core.service;

import java.time.LocalDate;
import java.util.List;

import com.example.my_campus_core.dto.ExamDto;
import com.example.my_campus_core.dto.LectureDto;
import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.ScheduleDto;
import com.example.my_campus_core.dto.request.ExamRequestDto;
import com.example.my_campus_core.dto.request.ScheduleRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.models.Exam;
import com.example.my_campus_core.models.TimeSlot;

public interface ScheduleService {

    ResponseDto createRoom(RoomDto roomDto);

    List<RoomDto> getAllRooms();

    Integer totalNumberRooms();

    List<RoomDto> getAllRoomsIgnoreRooms(List<Integer> roomIds);

    ResponseDto scheduleGenerationValidtaion(ScheduleRequestDto scheduleRequestDto);

    ScheduleDto scheduleGeneration(ScheduleRequestDto scheduleRequestDto, int weekOffset);

    List<LocalDate> schedulePageGeneration(int weekOffset);

    ScheduleDto getFullScheduleForWeek(int weekOffset);

    List<TimeSlot> getAvailableTimeSlotsForExam(String date);

    List<RoomDto> getAvailableRoomsForTimeSlot(String date, int timeSlotId);

    void addExamToSchedule(String date, Exam exam);

    ScheduleDto getScheduleForUserId(int userId, int weekOffset);

    List<LectureDto> getUpcomingLecturesforUserId(int userId);

    List<ExamDto> getUpcomingExamsForUserId(int userId);

    LectureDto getLectureById(int lectureId);
}
