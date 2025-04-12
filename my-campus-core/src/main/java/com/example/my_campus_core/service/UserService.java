package com.example.my_campus_core.service;

import com.example.my_campus_core.dto.UserDto;

public interface UserService {
    // Define the methods that will be implemented in the UserServiceImpl class
    void registerUser(UserDto userDto);

    void updateUserDetails(String username, String newDetails);

    void deleteUser(String username);
}
