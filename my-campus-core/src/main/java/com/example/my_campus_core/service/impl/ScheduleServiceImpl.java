package com.example.my_campus_core.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.LectureDto;
import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.ScheduleDto;
import com.example.my_campus_core.dto.request.ScheduleBodyDto;
import com.example.my_campus_core.dto.request.ScheduleRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.dto.response.ScheduleSolutionResponseDto;
import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Lecture;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.Schedule;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.repository.RoomRepository;
import com.example.my_campus_core.repository.ScheduleRepository;
import com.example.my_campus_core.repository.TimeSlotRepository;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.ScheduleService;
import com.example.my_campus_core.util.Mappers;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private RoomRepository roomRepository;
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private ScheduleRepository scheduleRepository;
    private TimeSlotRepository timeSlotRepository;
    private Mappers mapper;
    private com.example.my_campus_core.util.TimeUtil timeUtil;
    @Value("${schedule-service.url}")
    private String scheduleServiceUrl;

    @Autowired
    public ScheduleServiceImpl(RoomRepository roomRepository, CourseRepository courseRepository,
            UserRepository userRepository, ScheduleRepository scheduleRepository,
            TimeSlotRepository timeSlotRepository) {
        this.roomRepository = roomRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.timeSlotRepository = timeSlotRepository;
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
    public ScheduleDto scheduleGeneration(ScheduleRequestDto scheduleRequestDto, int weekOffset) {
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

        ScheduleSolutionResponseDto scheduleSolution = scheduleGenerationRequest(courses, rooms, timeSlots).getBody();

        System.out.println(scheduleSolution.getLectureList());
        List<LocalDate> weekDates = schedulePageGeneration(weekOffset);

        Schedule schedule = saveSchedule(scheduleSolution, weekDates.get(0), weekDates.get(4));
        ScheduleDto scheduleDto = Mappers.scheduleToScheduleDto(schedule);
        return scheduleDto;
    }

    public ResponseEntity<ScheduleSolutionResponseDto> scheduleGenerationRequest(
            List<CourseDto> courses,
            List<RoomDto> rooms,
            List<TimeSlot> timeSlots) {
        RestTemplate restTemplate = new RestTemplate();
        String url = scheduleServiceUrl + "/api/v1/schedule/generate";

        // Create request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ScheduleBodyDto scheduleBodyDto = new ScheduleBodyDto();
        scheduleBodyDto.setRooms(rooms);
        scheduleBodyDto.setCourses(courses);
        scheduleBodyDto.setTimeSlots(timeSlots);

        HttpEntity<ScheduleBodyDto> requestEntity = new HttpEntity<>(scheduleBodyDto, headers);

        // Make POST request
        ResponseEntity<ScheduleSolutionResponseDto> response = restTemplate.postForEntity(
                url,
                requestEntity,
                ScheduleSolutionResponseDto.class);

        return response;

    }

    public Schedule saveSchedule(ScheduleSolutionResponseDto scheduleSolution, LocalDate dateFrom, LocalDate dateTo) {
        Schedule schedule = new Schedule();
        List<Lecture> lectures = new ArrayList<>();
        for (LectureDto lecture : scheduleSolution.getLectureList()) {
            Lecture newLecture = new Lecture();
            newLecture.setCourse(courseRepository.findById(lecture.getCourse().getId()).orElseThrow(null));
            newLecture.setTimeSlot(lecture.getTimeSlot());
            newLecture.setRoom(lecture.getRoom());
            newLecture.setProfessor(userRepository.findById(lecture.getProfessor().getId()).orElseThrow(null));
            lectures.add(newLecture);
        }
        schedule.setLectureList(lectures);
        List<Room> rooms = new ArrayList<>();
        for (RoomDto room : scheduleSolution.getRoomList()) {
            Room newRoom = roomRepository.findById(room.getId()).orElseThrow(null);
            rooms.add(newRoom);
        }
        schedule.setRoomList(rooms);
        List<TimeSlot> timeSlots = new ArrayList<>();

        for (TimeSlot timeSlot : scheduleSolution.getTimeSlotList()) {
            TimeSlot newTimeSlot = timeSlotRepository.findById(timeSlot.getId()).orElseThrow(null);
            timeSlots.add(newTimeSlot);
        }
        schedule.setTimeSlotList(timeSlots);
        schedule.setDateFrom(dateFrom);
        schedule.setDateTo(dateTo);
        return scheduleRepository.save(schedule);
    }

    @Override
    public List<LocalDate> schedulePageGeneration(int weekOffset) {
        List<LocalDate> workdays = timeUtil.getCurrentWeekWorkdays(weekOffset);
        return workdays;
    }

    @Override
    public ScheduleDto getFullScheduleForWeek(int weekOffset) {
        List<LocalDate> datesInWeek = schedulePageGeneration(weekOffset);
        Schedule schedule = scheduleRepository.findByDateFromAndDateTo(datesInWeek.get(0), datesInWeek.get(4));
        try {
            ScheduleDto scheduleDto = Mappers.scheduleToScheduleDto(schedule);
            return scheduleDto;
        } catch (Exception e) {
            System.err.println("Error" + e);
            return null;
        }
    }

    @Override
    public List<TimeSlot> getAvailableTimeSlotsForExam(String date) {
        LocalDate today = LocalDate.parse(date);
        List<LocalDate> datesFromAndTo = timeUtil.getWeekStartAndEnd(today);
        Schedule schedule = scheduleRepository
                .findByDateFromAndDateTo(datesFromAndTo.get(0), datesFromAndTo.get(1));
        if (schedule == null) {
            List<TimeSlot> availableSlots = timeSlotRepository.findAllByDayOfWeek(today.getDayOfWeek());
            return availableSlots;
        }
        List<TimeSlot> allSlotsForToday = schedule.getTimeSlotList().stream()
                .filter(slot -> slot.getDayOfWeek() == today.getDayOfWeek()).collect(Collectors.toList());
        List<TimeSlot> allBookedSlotsForToday = schedule.getLectureList().stream()
                .filter(lecture -> lecture.getTimeSlot().getDayOfWeek() == today.getDayOfWeek())
                .map(Lecture::getTimeSlot).collect(Collectors.toList());

        List<TimeSlot> availableSlots = allSlotsForToday.stream().filter(slot -> !allBookedSlotsForToday.contains(slot))
                .collect(Collectors.toList());
        return availableSlots;
    }

    @Override
    public List<RoomDto> getAvailableRoomsForTimeSlot(String date, int timeSlotId) {
        LocalDate today = LocalDate.parse(date);
        List<LocalDate> datesFromAndTo = timeUtil.getWeekStartAndEnd(today);
        Schedule schedule = scheduleRepository
                .findByDateFromAndDateTo(datesFromAndTo.get(0), datesFromAndTo.get(1));
        // If no schedule exists, all rooms are available
        if (schedule == null) {
            return roomRepository.findAll().stream()
                    .map(room -> mapper.roomToRoomDto(room))
                    .collect(Collectors.toList());
        }
        TimeSlot desiredTimeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid timeSlotId"));
        return roomRepository.findAll().stream()
                .map(room -> mapper.roomToRoomDto(room))
                .filter(room -> isRoomAvailable(room, desiredTimeSlot, schedule))
                .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(RoomDto room, TimeSlot desiredTimeSlot, Schedule schedule) {
        List<Lecture> lecturesInRoom = schedule.getLectureList().stream()
                .filter(lecture -> lecture.getRoom().getId() == room.getId()).collect(Collectors.toList());
        List<Lecture> lecturesInRoomOnTimeSlot = lecturesInRoom.stream()
                .filter(lecture -> lecture.getTimeSlot().getId() == desiredTimeSlot.getId())
                .collect(Collectors.toList());
        if (lecturesInRoomOnTimeSlot.size() == 0) {
            return true;
        }
        return false;
    }
}
