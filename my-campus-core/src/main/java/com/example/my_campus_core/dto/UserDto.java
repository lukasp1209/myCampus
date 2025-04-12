package com.example.my_campus_core.dto;

import java.util.Date;

import com.example.my_campus_core.models.Address;

import lombok.Data;

@Data
public class UserDto {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private Address address;
    private Date birthDate;
    private String role;
}
