package com.example.my_campus_core.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.my_campus_core.service.ScheduleService;

@Controller
public class HomeController {
    private ScheduleService scheduleService;

    public HomeController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/test")
    public String getTestPage(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
            Model model) {
        model.addAttribute("solution", scheduleService.generateScheduleSolution(null, null, null));
        return "./home";
    }

    @GetMapping("/")
    public String getHomePage() {
        return "./home"; // Return the name of the home view (e.g., index.html)
    }

}
