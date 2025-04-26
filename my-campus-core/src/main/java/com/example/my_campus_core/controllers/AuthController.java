package com.example.my_campus_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.my_campus_core.dto.LoginDto;
import com.example.my_campus_core.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "./login"; // Return the name of the login view (e.g., login.html)
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto) {
        System.out.println(
                "Login attempt with email: " + loginDto.getEmail() + " and password: " + loginDto.getPassword());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/"; // Redirect to the dashboard or home page after successful login
        } catch (Exception e) {
            System.err.println("Invalid email or password: " + e.getMessage());
            return "redirect:/login?error"; // Redirect back to the login page with an error message

        }

    }

}
