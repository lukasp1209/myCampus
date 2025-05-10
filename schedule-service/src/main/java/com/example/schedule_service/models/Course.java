package com.example.schedule_service.models;

import java.util.List;

import lombok.Data;

@Data
public class Course {
    private int id;
    private String name;
    private String description;

    private int professorId;
    private User professor;
    private List<Integer> studentsIds;
    private List<User> students;
}
