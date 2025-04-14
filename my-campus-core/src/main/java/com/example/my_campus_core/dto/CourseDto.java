package com.example.my_campus_core.dto;

import java.util.List;

import com.example.my_campus_core.models.UserEntity;

import lombok.Data;

@Data
public class CourseDto {
    private int id;
    private String name;
    private String description;

    private int professorId;
    private UserEntity professor;
    private List<Integer> studentsIds;
    private List<UserEntity> students;
}
