package com.example.my_campus_core.dto;

import com.example.my_campus_core.models.Address;

import lombok.Data;

@Data
public class UserDto {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private Address address;
    private String birthDate;
    private String role;
}
