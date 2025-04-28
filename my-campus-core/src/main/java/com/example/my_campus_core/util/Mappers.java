package com.example.my_campus_core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.ExamDto;
import com.example.my_campus_core.dto.LectureDto;
import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.ScheduleDto;
import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Exam;
import com.example.my_campus_core.models.Lecture;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.models.Schedule;
import com.example.my_campus_core.models.UserEntity;

@Component
public class Mappers {
    public static UserDto userEntityToDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setFirstName(userEntity.getFirstName());
        userDto.setLastName(userEntity.getLastName());
        userDto.setEmail(userEntity.getEmail());
        userDto.setAddress(userEntity.getAddress());
        userDto.setStatus(userEntity.getStatus());
        userDto.setBirthDate(userEntity.getBirthDate());
        userDto.setRole(userEntity.getRole());
        return userDto;
    }

    public static RoomDto roomToRoomDto(Room room) {
        RoomDto roomDto = new RoomDto();
        roomDto.setId(room.getId());
        roomDto.setName(room.getName());
        return roomDto;
    }

    public static CourseDto courseToCourseDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setDescription(course.getDescription());
        courseDto.setProfessorId(course.getProfessor().getId());
        courseDto.setStudentsIds(course.getStudents().stream().map(student -> student.getId()).toList());
        courseDto.setProfessor(userEntityToDto(course.getProfessor()));
        List<UserDto> students = course.getStudents().stream().map(student -> userEntityToDto(student))
                .toList();
        courseDto.setStudents(students);

        return courseDto;
    }

    public static LectureDto lectureToLectureDto(Lecture lecture) {
        LectureDto lectureDto = new LectureDto();
        lectureDto.setId(lecture.getId());
        lectureDto.setCourse(lecture.getCourse());
        lectureDto.setProfessor(userEntityToDto(lecture.getProfessor()));
        lectureDto.setRoom(lecture.getRoom());
        lectureDto.setTimeSlot(lecture.getTimeSlot());

        return lectureDto;
    }

    public static ScheduleDto scheduleToScheduleDto(Schedule schedule) {
        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setId(schedule.getId());
        scheduleDto.setDateFrom(schedule.getDateFrom());
        scheduleDto.setDateTo(schedule.getDateTo());

        List<LectureDto> lectureDtos = new ArrayList<>();
        for (Lecture lecture : schedule.getLectureList()) {
            LectureDto lectureDto = lectureToLectureDto(lecture);
            lectureDtos.add(lectureDto);
        }
        scheduleDto.setLectureList(lectureDtos);
        List<RoomDto> roomDtos = new ArrayList<>();

        for (Room room : schedule.getRoomList()) {
            RoomDto newRoomDto = roomToRoomDto(room);
            roomDtos.add(newRoomDto);
        }
        List<ExamDto> examDtos = new ArrayList<>();
        for (Exam exam : schedule.getExamsList()) {
            ExamDto examDto = examToExamDto(exam);
            examDtos.add(examDto);
        }
        scheduleDto.setRoomList(roomDtos);
        scheduleDto.setTimeSlotList(schedule.getTimeSlotList());
        scheduleDto.setExamList(examDtos);
        return scheduleDto;
    }

    public static ExamDto examToExamDto(Exam exam) {
        ExamDto examDto = new ExamDto();
        examDto.setId(exam.getId());
        examDto.setCourse(courseToCourseDto(exam.getCourse()));
        examDto.setExamDate(exam.getExamDate());
        examDto.setRoom(roomToRoomDto(exam.getRoom()));
        examDto.setTimeSlot(exam.getTimeSlot());
        examDto.setProfessor(userEntityToDto(exam.getProfessor()));
        examDto.setDescription(exam.getDescription());
        examDto.setAllStudents(
                exam.getAllStudents().stream().map(student -> userEntityToDto(student)).collect(Collectors.toList()));
        examDto.setEnrolledStudents(exam.getEnrolledStudents().stream().map(student -> userEntityToDto(student))
                .collect(Collectors.toList()));
        return examDto;
    }
}