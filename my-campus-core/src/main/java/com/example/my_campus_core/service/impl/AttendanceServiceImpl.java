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

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private UserRepository userRepository;
    private LectureRepository lectureRepository;

    @Autowired
    public AttendanceServiceImpl(ScheduleRepository scheduleRepository, LectureRepository lectureRepository,
            UserRepository userRepository) {
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
    }

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
