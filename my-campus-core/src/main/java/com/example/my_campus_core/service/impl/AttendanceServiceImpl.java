package com.example.my_campus_core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.dto.AttendanceDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.exceptions.NotFoundException;
import com.example.my_campus_core.models.Lecture;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.LectureRepository;
import com.example.my_campus_core.repository.ScheduleRepository;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.AttendanceService;

/**
 * Implementation of the AttendanceService interface that handles attendance-related operations.
 * This service manages student attendance tracking for lectures.
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {

    private UserRepository userRepository;
    private LectureRepository lectureRepository;

    /**
     * Constructs a new AttendanceServiceImpl with required dependencies.
     *
     * @param scheduleRepository Repository for schedule-related operations
     * @param lectureRepository Repository for lecture-related operations
     * @param userRepository Repository for user-related operations
     */
    @Autowired
    public AttendanceServiceImpl(ScheduleRepository scheduleRepository, LectureRepository lectureRepository,
            UserRepository userRepository) {
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
    }

    /**
     * Checks if a user is marked as attending a specific lecture.
     *
     * @param lectureId The ID of the lecture to check
     * @param userId The ID of the user to check
     * @return AttendanceDto containing the attendance status
     * @throws NotFoundException if the user or lecture is not found
     */
    @Override
    public AttendanceDto amIMarkedAsAttending(int lectureId, int userId) {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setLectureNow(true);
        UserEntity currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found."));
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new NotFoundException("Lecture with ID: " + lectureId + " not found."));
        if (lecture.getAttendedStudents().contains(currentUser)) {
            attendanceDto.setAttending(true);
        } else {
            attendanceDto.setAttending(false);
        }
        attendanceDto.setLectureId(lectureId);
        attendanceDto.setUserId(userId);
        return attendanceDto;
    }

    /**
     * Marks a user as attending a specific lecture.
     *
     * @param lectureId The ID of the lecture
     * @param userId The ID of the user to mark as attending
     * @return ResponseDto indicating the result of the operation
     * @throws NotFoundException if the user or lecture is not found
     */
    @Override
    public ResponseDto setAsAttending(int lectureId, int userId) {
        UserEntity currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found."));
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new NotFoundException("Lecture with ID: " + lectureId + " not found."));
        List<UserEntity> attendingStudents = lecture.getAttendedStudents();
        attendingStudents.add(currentUser);
        lecture.setAllStudents(attendingStudents);
        lectureRepository.saveAndFlush(lecture);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus("success");
        responseDto.setMessage("You are marked for lecture " + lecture.getCourse().getName() + " as attenidng.");
        return responseDto;
    }

}
