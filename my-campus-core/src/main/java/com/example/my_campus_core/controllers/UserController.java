package com.example.my_campus_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.security.CustomUserDetailsService;
import com.example.my_campus_core.security.SecurityUtil;
import com.example.my_campus_core.service.UserService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private UserService userService;
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserController(
            UserService userService,
            CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
        this.userService = userService;
    }

    @GetMapping("/user")
    public String showUserManagmentPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        // List<User> userPage = userService.getUsers(page, 20); // 20 users per page
        model.addAttribute("users", userService.getUsersAsAdmin(page));
        model.addAttribute("page", 0);

        model.addAttribute("totalPages", userService.totalUsers(size)); // Calculate total pages

        return "./userManagement"; // Return the name of the user view (e.g., user.html)
    }

    @GetMapping("/user/{userId}")
    public String getUserProfile(@PathVariable int userId, Model model) {

        model.addAttribute("user", userService.getUserById(userId));
        model.addAttribute("isHisProfile",
                customUserDetailsService.isHisProfile(SecurityUtil.getSessionUser(), userId));
        return "./userProfile"; // Return the name of the user profile view (e.g., userProfile.html)
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
