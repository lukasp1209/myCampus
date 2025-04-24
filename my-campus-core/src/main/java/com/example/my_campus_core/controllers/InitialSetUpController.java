package com.example.my_campus_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.my_campus_core.dto.InitialSetUpDto;
import com.example.my_campus_core.util.InitialSetUp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class InitialSetUpController {
    private InitialSetUp initialSetUp;

    @Autowired
    public InitialSetUpController(InitialSetUp initialSetUp) {
        this.initialSetUp = initialSetUp;
    }

    @GetMapping("/setup")
    public String getInitialSetupPage(Model model) {
        model.addAttribute("response", initialSetUp.initialSetUp());
        return "./setup";
    }

    @PostMapping("/setup")
    public String InitialSetUp(@ModelAttribute InitialSetUpDto initialSetUpDto, RedirectAttributes redirectAttributes) {
        System.out.println("NEW USER: " + initialSetUpDto);
        redirectAttributes.addAttribute("response", initialSetUp.initialUserAndTimeSlots(initialSetUpDto));
        return "redirect:/login";
    }

}
