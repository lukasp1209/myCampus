package com.example.my_campus_core.dto;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.my_campus_core.models.Address;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserDto {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private Address address;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String role;
}
