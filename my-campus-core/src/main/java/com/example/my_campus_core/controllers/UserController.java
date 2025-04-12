package com.example.my_campus_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.service.UserService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/register")
    public String showRegistrationForm() {
        return "./register"; // Return the name of the registration view (e.g., register.html)
    }

    @PostMapping("/user/register")
    public String registerUser(@ModelAttribute UserDto userDto) {
        System.out.println(userDto);
        userService.registerUser(userDto);
        return "./register";
    }

}
