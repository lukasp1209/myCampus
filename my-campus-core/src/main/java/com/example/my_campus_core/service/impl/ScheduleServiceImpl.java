package com.example.my_campus_core.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
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
import com.example.my_campus_core.dto.ExamDto;
import com.example.my_campus_core.dto.LectureDto;
import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.ScheduleDto;
import com.example.my_campus_core.dto.request.ScheduleBodyDto;
import com.example.my_campus_core.dto.request.ScheduleRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.dto.response.ScheduleSolutionResponseDto;
import com.example.my_campus_core.exceptions.AccessDeniedException;
import com.example.my_campus_core.exceptions.InternalErrorException;
import com.example.my_campus_core.exceptions.NotFoundException;
import com.example.my_campus_core.exceptions.UnsupportedEntityException;
import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Exam;
import com.example.my_campus_core.models.Lecture;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.Schedule;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.CourseRepository;
import com.example.my_campus_core.repository.ExamRepository;
import com.example.my_campus_core.repository.LectureRepository;
import com.example.my_campus_core.repository.RoomRepository;
import com.example.my_campus_core.repository.ScheduleRepository;
import com.example.my_campus_core.repository.TimeSlotRepository;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.security.CustomUserDetailsService;
import com.example.my_campus_core.security.SecurityUtil;
import com.example.my_campus_core.service.ScheduleService;
import com.example.my_campus_core.util.Mappers;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private RoomRepository roomRepository;
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private ScheduleRepository scheduleRepository;
    private TimeSlotRepository timeSlotRepository;
    private LectureRepository lectureRepository;
    private CustomUserDetailsService customUserDetailsService;
    private ExamRepository examRepository;
    private Mappers mapper;
    private com.example.my_campus_core.util.TimeUtil timeUtil;
    @Value("${schedule-service.url}")
    private String scheduleServiceUrl;

    @Autowired
    public ScheduleServiceImpl(RoomRepository roomRepository, CourseRepository courseRepository,
            UserRepository userRepository, ScheduleRepository scheduleRepository,
            TimeSlotRepository timeSlotRepository, CustomUserDetailsService customUserDetailsService,
            LectureRepository lectureRepository, ExamRepository examRepository) {
        this.roomRepository = roomRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.lectureRepository = lectureRepository;
        this.examRepository = examRepository;
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
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong! \\n" + //
                            " Please try again or contact platform support!"));
            CourseDto courseDto = mapper.courseToCourseDto(course);
            courses.add(courseDto);
        }
        for (Integer roomId : scheduleRequestDto.getRoomIds()) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong! \\n" + //
                            " Please try again or contact platform support!"));
            RoomDto roomDto = mapper.roomToRoomDto(room);
            rooms.add(roomDto);
        }
        ScheduleSolutionResponseDto scheduleSolution = scheduleGenerationRequest(courses, rooms, timeSlots).getBody();
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
            newLecture.setCourse(courseRepository.findById(lecture.getCourse().getId())
                    .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong! \\n" + //
                            " Please try again or contact platform support!")));
            newLecture.setTimeSlot(lecture.getTimeSlot());
            newLecture.setRoom(lecture.getRoom());
            newLecture.setProfessor(userRepository.findById(lecture.getProfessor().getId())
                    .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong! \\n" + //
                            " Please try again or contact platform support!")));
            newLecture.setDate(dateFrom.with(TemporalAdjusters.nextOrSame(lecture.getTimeSlot().getDayOfWeek())));
            newLecture.setAllStudents(newLecture.getCourse().getStudents());
            lectures.add(newLecture);
        }
        schedule.setLectureList(lectures);
        List<Room> rooms = new ArrayList<>();
        for (RoomDto room : scheduleSolution.getRoomList()) {
            Room newRoom = roomRepository.findById(room.getId())
                    .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong! \\n" + //
                            " Please try again or contact platform support!"));
            rooms.add(newRoom);
        }
        schedule.setRoomList(rooms);
        List<TimeSlot> timeSlots = new ArrayList<>();

        for (TimeSlot timeSlot : scheduleSolution.getTimeSlotList()) {
            TimeSlot newTimeSlot = timeSlotRepository.findById(timeSlot.getId())
                    .orElseThrow(() -> new InternalErrorException("Oops! Something went wrong! \\n" + //
                            " Please try again or contact platform support!"));
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
                .orElseThrow(() -> new UnsupportedEntityException("Invalid time slot ID " + timeSlotId));
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

    @Override
    public void addExamToSchedule(String date, Exam exam) {
        LocalDate today = LocalDate.parse(date);
        List<LocalDate> datesFromAndTo = timeUtil.getWeekStartAndEnd(today);

        Schedule schedule = scheduleRepository.findByDateFromAndDateTo(datesFromAndTo.get(0), datesFromAndTo.get(1));
        if (schedule == null) {
            // Add some logice to create new schedule
            Schedule newSchedule = new Schedule();
            newSchedule.setDateFrom(datesFromAndTo.get(0));
            newSchedule.setDateTo(datesFromAndTo.get(1));

            newSchedule.setLectureList(new ArrayList<>());
            List<Exam> examList = new ArrayList<>();
            examList.add(exam);
            newSchedule.setExamsList(examList);
            newSchedule.setRoomList(roomRepository.findAll());
            newSchedule.setTimeSlotList(timeSlotRepository.findAll());
            scheduleRepository.saveAndFlush(newSchedule);
            return;

        }
        List<Exam> examList = schedule.getExamsList();
        examList.add(exam);
        schedule.setExamsList(examList);
        scheduleRepository.saveAndFlush(schedule);
    }

    @Override
    public ScheduleDto getScheduleForUserId(int userId, int weekOffset) {
        isHisProfile(userId);
        ScheduleDto scheduleDto = getFullScheduleForWeek(weekOffset);
        if (scheduleDto != null) {
            scheduleDto.setLectureList(scheduleDto.getLectureList().stream()
                    .filter(lecture -> userIsInLecture(lecture, userId)).collect(Collectors.toList()));
            scheduleDto.setExamList(scheduleDto.getExamList().stream().filter(exam -> userIsInExam(exam, userId))
                    .collect(Collectors.toList()));
        }
        return scheduleDto;
    }

    public boolean userIsInLecture(LectureDto lecture, int userId) {
        if (lecture.getProfessor().getId() == userId)
            return true;
        if (lecture.getCourse().getStudents().stream().anyMatch(student -> student.getId() == userId))
            return true;
        return false;
    }

    public boolean userIsInExam(ExamDto examDto, int userId) {
        if (examDto.getProfessor().getId() == userId)
            return true;
        if (examDto.getAllStudents().stream().anyMatch(student -> student.getId() == userId))
            return true;
        return false;
    }

    public boolean isHisProfile(int userId) {
        boolean isHisProfile = customUserDetailsService.isHisProfile(SecurityUtil.getSessionUser(), userId);
        if (!isHisProfile) {
            throw new AccessDeniedException("User with ID:" + userId + " can't access this schedule!");
        }
        return isHisProfile;
    }

    @Override
    public List<LectureDto> getUpcomingLecturesforUserId(int userId) {
        isHisProfile(userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new InternalErrorException("Internal Error"));
        if ("ROLE_PROFESOR".equals(user.getRole())) {
            return lectureRepository
                    .findTop3ByProfessor_IdAndDateGreaterThanEqualOrderByDateAsc(userId, LocalDate.now()).stream()
                    .map(lecture -> mapper.lectureToLectureDto(lecture)).collect(Collectors.toList());
        }
        if ("ROLE_STUDENT".equals(user.getRole())) {
            return lectureRepository
                    .findTop3ByAllStudents_IdAndDateGreaterThanEqualOrderByDateAsc(userId, LocalDate.now()).stream()
                    .map(lecture -> mapper.lectureToLectureDto(lecture)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<ExamDto> getUpcomingExamsForUserId(int userId) {
        isHisProfile(userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new InternalErrorException("Internal Error"));
        if ("ROLE_PROFESOR".equals(user.getRole())) {
            return examRepository
                    .findTop3ByProfessor_IdAndExamDateGreaterThanEqualOrderByExamDateAsc(userId, LocalDate.now())
                    .stream()
                    .map(exam -> mapper.examToExamDto(exam)).collect(Collectors.toList());
        }
        if ("ROLE_STUDENT".equals(user.getRole())) {
            return examRepository
                    .findTop3ByAllStudents_IdAndExamDateGreaterThanEqualOrderByExamDateAsc(userId, LocalDate.now())
                    .stream()
                    .map(exam -> mapper.examToExamDto(exam)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public LectureDto getLectureById(int lectureId) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new NotFoundException("Lecture with id: " + lectureId + " not found"));

        return mapper.lectureToLectureDto(lecture);
    }

    @Override
    public void updateScheduleOnCourseUpdate(Course course) {
        if (course == null)
            return;

        List<UserEntity> students = course.getStudents();
        List<Lecture> lectures = lectureRepository.findAllByCourse_Id(course.getId());
        List<Exam> exams = examRepository.findAllByCourseId(course.getId());
        if (lectures != null && !lectures.isEmpty()) {
            lectures.forEach(lecture -> lecture.setAllStudents(students));
            lectureRepository.saveAllAndFlush(lectures);
        }
        if (exams != null && !exams.isEmpty()) {
            exams.forEach(exam -> exam.setAllStudents(students));
            examRepository.saveAllAndFlush(exams);
        }

    }

}
