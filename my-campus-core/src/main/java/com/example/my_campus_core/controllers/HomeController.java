package com.example.my_campus_core.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String getHomePage() {
        return "./home"; // Return the name of the home view (e.g., index.html)
    }

}
