package com.example.my_campus_core.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.my_campus_core.dto.CourseDto;
import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.models.Course;
import com.example.my_campus_core.models.Room;
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
}