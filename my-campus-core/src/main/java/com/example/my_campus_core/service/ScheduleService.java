package com.example.my_campus_core.service;

import java.time.LocalDate;
import java.util.List;

import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.ScheduleDto;
import com.example.my_campus_core.dto.request.ScheduleRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;

public interface ScheduleService {

    ResponseDto createRoom(RoomDto roomDto);

    List<RoomDto> getAllRooms();

    Integer totalNumberRooms();

    List<RoomDto> getAllRoomsIgnoreRooms(List<Integer> roomIds);

    ResponseDto scheduleGenerationValidtaion(ScheduleRequestDto scheduleRequestDto);

    ScheduleDto scheduleGeneration(ScheduleRequestDto scheduleRequestDto, int weekOffset);

    List<LocalDate> schedulePageGeneration(int weekOffset);
}
