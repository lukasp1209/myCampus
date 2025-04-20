package com.example.my_campus_core.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.dto.request.ScheduleBodyDto;
import com.example.my_campus_core.dto.request.ScheduleRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.repository.RoomRepository;
import com.example.my_campus_core.service.ScheduleService;
import com.example.my_campus_core.util.Mappers;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private RoomRepository roomRepository;
    private CourseRepository courseRepository;
    private Mappers mapper;
    private com.example.my_campus_core.util.TimeUtil timeUtil;

    @Autowired
    public ScheduleServiceImpl(RoomRepository roomRepository, CourseRepository courseRepository) {
        this.roomRepository = roomRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public ResponseDto createRoom(RoomDto roomDto) {
        ResponseDto responseDto = new ResponseDto();
        Room room = new Room();
        room.setName(roomDto.getName());
        try {
            roomRepository.save(room);
            responseDto.setMessage("Room " + roomDto.getName() + " created successfully");
            responseDto.setStatus("success");
        } catch (Exception e) {
            responseDto.setMessage("Error creating room: " + e.getMessage());
            responseDto.setStatus("error");
        }
        return responseDto;
    }

    @Override
    public List<RoomDto> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        if (rooms.isEmpty()) {
            return List.of();
        }
        return rooms.stream().map(room -> mapper.roomToRoomDto(room)).toList();
    }

    @Override
    public Integer totalNumberRooms() {
        int totalRooms = roomRepository.findAll().size();
        return totalRooms;
    }

    @Override
    public List<RoomDto> getAllRoomsIgnoreRooms(List<Integer> roomIds) {
        List<Room> rooms = roomRepository.findAllByIdNotIn(roomIds);
        if (rooms.isEmpty()) {
            return List.of();
        }
        return rooms.stream().filter(room -> !roomIds.contains(room.getId())).map(room -> mapper.roomToRoomDto(room))
                .toList();
    }

    @Override
    public ResponseDto scheduleGenerationValidtaion(ScheduleRequestDto scheduleRequestDto) {
        ResponseDto responseDto = new ResponseDto();
        if (scheduleRequestDto.getRoomIds() == null || scheduleRequestDto.getRoomIds().isEmpty()) {
            responseDto.setMessage("You have to Select at least one room");
            responseDto.setStatus("error");
            return responseDto;
        }

        if (scheduleRequestDto.getCourseIds() == null || scheduleRequestDto.getCourseIds().isEmpty()) {
            responseDto.setMessage("You have to Select at least one course");
            responseDto.setStatus("error");
            return responseDto;
        }

        responseDto.setMessage("Schedule generated successfully");
        responseDto.setStatus("success");
        return responseDto;

    }

    @Override
    public void scheduleGeneration(ScheduleRequestDto scheduleRequestDto, int weekOffset) {
        List<CourseDto> courses = new ArrayList<>();
        List<RoomDto> rooms = new ArrayList<>();
        List<TimeSlot> timeSlots = new ArrayList<>();
        int slotCounter = 1;
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.getValue() >= DayOfWeek.MONDAY.getValue() &&
                    day.getValue() <= DayOfWeek.FRIDAY.getValue()) {
                for (int i = 0; i < 3; i++) {
                    TimeSlot slot = new TimeSlot();
                    slot.setId(slotCounter);
                    slot.setDayOfWeek(day);
                    slot.setStartTime(LocalTime.of(9, 0).plusHours(i * 3));
                    slot.setEndTime(LocalTime.of(12, 0).plusHours(i * 3));
                    timeSlots.add(slot);
                    slotCounter += 1;
                }

            }
        }

        for (Integer courseId : scheduleRequestDto.getCourseIds()) {
            Course course = courseRepository.findById(courseId).orElseThrow(null);
            CourseDto courseDto = mapper.courseToCourseDto(course);
            courses.add(courseDto);
        }

        for (Integer roomId : scheduleRequestDto.getRoomIds()) {
            Room room = roomRepository.findById(roomId).orElseThrow(null);
            RoomDto roomDto = mapper.roomToRoomDto(room);
            rooms.add(roomDto);
        }

        scheduleGenerationRequest(courses, rooms, timeSlots);
    }

    public void scheduleGenerationRequest(
            List<CourseDto> courses,
            List<RoomDto> rooms,
            List<TimeSlot> timeSlots) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/api/v1/schedule/generate";

        // Create request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ScheduleBodyDto scheduleBodyDto = new ScheduleBodyDto();
        scheduleBodyDto.setRooms(rooms);
        scheduleBodyDto.setCourses(courses);
        scheduleBodyDto.setTimeSlots(timeSlots);

        HttpEntity<ScheduleBodyDto> requestEntity = new HttpEntity<>(scheduleBodyDto, headers);

        // Make POST request
        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                requestEntity,
                String.class);

        System.out.println("Response: " + response);

    }

    @Override
    public List<LocalDate> schedulePageGeneration(int weekOffset) {
        List<LocalDate> workdays = timeUtil.getCurrentWeekWorkdays(weekOffset);
        return workdays;
    }
}
