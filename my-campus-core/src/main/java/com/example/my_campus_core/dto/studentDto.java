package com.example.my_campus_core.dto;

import java.util.List;

import com.example.my_campus_core.models.Course;

import lombok.Data;

@Data
public class studentDto extends UserDto {
    private List<Course> courses;
}
