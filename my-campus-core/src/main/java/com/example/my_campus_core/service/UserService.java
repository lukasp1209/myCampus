package com.example.my_campus_core.service;

import java.util.List;

import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.dto.response.ResponseDto;

public interface UserService {
    // Define the methods that will be implemented in the UserServiceImpl class
    List<ResponseDto> registerUser(UserDto userDto);

    List<UserDto> getUsersAsAdmin(int page);

    int totalUsers(int size);

    UserDto getUserById(int userId);

    List<UserDto> getUsersByNameAndRole(String name, String role);

    ResponseDto changeUserStatus(int userId, String status);

    UserDto getUserByEmail(String email);

    ResponseDto editUserProfile(UserDto userDto);

}
