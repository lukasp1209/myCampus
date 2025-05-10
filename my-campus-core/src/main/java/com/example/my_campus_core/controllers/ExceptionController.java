package com.example.my_campus_core.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExceptionController {
    @GetMapping("/exception")
    public String showErrorPage(Model model) {
        return "./error"; // Your Thymeleaf/HTML template name
    }

}
