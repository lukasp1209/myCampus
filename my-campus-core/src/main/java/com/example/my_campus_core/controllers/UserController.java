package com.example.my_campus_core.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.security.CustomUserDetailsService;
import com.example.my_campus_core.security.SecurityUtil;
import com.example.my_campus_core.service.UserService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public String getUserManagmentPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        // List<User> userPage = userService.getUsers(page, 20); // 20 users per page
        model.addAttribute("users", userService.getUsersAsAdmin(page));
        model.addAttribute("page", page);

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

    @GetMapping("/user/{userId}/edit")
    public String getEditUserPage(@PathVariable int userId, Model model) {
        if (customUserDetailsService.isHisProfile(SecurityUtil.getSessionUser(), userId)) {
            model.addAttribute("user", userService.getUserById(userId));
            return "./editUser"; // Return the name of the edit user view (e.g., editUser.html)
        } else {
            return "redirect:/"; // Redirect to the user management page if not authorized
        }
    }

    @PostMapping("/user/{userId}/edit")
    public String editUser(@PathVariable int userId, @ModelAttribute UserDto userDto,
            RedirectAttributes redirectAttributes) {
        userDto.setId(userId);
        ResponseDto response = userService.editUserProfile(userDto);
        redirectAttributes.addFlashAttribute("response", response);
        return "redirect:/user/" + userId; // Redirect to the user's profile page after editing
    }

    @GetMapping("/user/register")
    public String showRegistrationForm() {
        return "./register"; // Return the name of the registration view (e.g., register.html)
    }

    @PostMapping("/user/register")
    public String registerUser(@ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        List<ResponseDto> response = userService.registerUser(userDto);
        ResponseDto toast = response.get(0); // Get the toast message
        ResponseDto infoMessage = response.get(1); // Get the info message
        redirectAttributes.addFlashAttribute("response", toast);
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage); // Add flash attributes for messages
        return "redirect:/user/register";
    }

    @PostMapping("/user/{userId}/status/change")
    public String changeUserStatus(@PathVariable int userId, @RequestParam String status, Model model,
            RedirectAttributes redirectAttributes) {
        ResponseDto response = userService.changeUserStatus(userId, status); // Change the user's status
        redirectAttributes.addFlashAttribute("response", response); // Add a flash attribute for the message
        return "redirect:/user"; // Redirect to the user management page after changing status
    }

}
