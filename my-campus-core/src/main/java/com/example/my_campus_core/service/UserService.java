package com.example.my_campus_core.service;

import java.util.List;

import com.example.my_campus_core.dto.UserDto;

public interface UserService {
    // Define the methods that will be implemented in the UserServiceImpl class
    void registerUser(UserDto userDto);

    List<UserDto> getUsersAsAdmin(int page);

    int totalUsers(int size);

    UserDto getUserById(int userId);

    List<UserDto> getUsersByNameAndRole(String name, String role); // Add this method to the interface

}
