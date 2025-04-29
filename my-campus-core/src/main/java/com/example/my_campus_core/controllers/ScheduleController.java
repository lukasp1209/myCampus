package com.example.my_campus_core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.request.ScheduleRequestDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.service.ScheduleService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ScheduleController {
    private ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("schedule/managment")
    public String getScheduleManagmentPage(@RequestParam(defaultValue = "0") int weekOffset, Model model) {
        model.addAttribute("datesInWeek", scheduleService.schedulePageGeneration(weekOffset));
        model.addAttribute("schedule", scheduleService.getFullScheduleForWeek(weekOffset));
        return "./scheduleManagment";
    }

    @GetMapping("schedule/managment/rooms")
    public String getScheduleManagmentRoomsPage(Model model) {
        model.addAttribute("rooms", scheduleService.getAllRooms());
        model.addAttribute("totalRooms", scheduleService.totalNumberRooms());
        return "./scheduleManagmentRooms";
    }

    @GetMapping("/schedule/managment/schedule/add")
    public String getScheduleWeeklySchedulePage(Model model) {
        model.addAttribute("rooms", scheduleService.getAllRooms());
        return "./scheduleWeeklySchedule";
    }

    @GetMapping("/schedule/managment/schedule/generated")
    public String getGeneratedSchedulePage() {
        return "./generatedSchedule";
    }

    @GetMapping("/schedule/{userId}")
    public String getSchedulePage(@PathVariable int userId, @RequestParam(defaultValue = "0") int weekOffset,
            Model model) {
        model.addAttribute("datesInWeek", scheduleService.schedulePageGeneration(weekOffset));
        model.addAttribute("schedule", scheduleService.getScheduleForUserId(userId, weekOffset));
        model.addAttribute("upcomingLectures", scheduleService.getUpcomingLecturesforUserId(userId));
        model.addAttribute("upcomingExams", scheduleService.getUpcomingExamsForUserId(userId));
        return "./schedule";
    }

    @PostMapping("/schedule/managment/schedule/add")
    public String makeSchedule(@RequestParam(defaultValue = "0") int weekOffset,
            @ModelAttribute ScheduleRequestDto scheduleRequestDto,
            RedirectAttributes redirectAttributes) {

        System.out.println("Schedule:  " + scheduleRequestDto);
        ResponseDto responseDto = scheduleService.scheduleGenerationValidtaion(scheduleRequestDto);
        if (responseDto.getStatus().equals("error")) {
            redirectAttributes.addFlashAttribute("response", responseDto);
            return "redirect:/schedule/managment/schedule/add";
        }
        redirectAttributes.addFlashAttribute("schedule",
                scheduleService.scheduleGeneration(scheduleRequestDto, weekOffset));
        redirectAttributes.addFlashAttribute("response", responseDto);
        return "redirect:/schedule/managment/schedule/generated";
    }

    @PostMapping("schedule/managment/rooms/add")
    public String createRoom(@ModelAttribute RoomDto roomDto, RedirectAttributes redirectAttributes) {
        ResponseDto responseDto = scheduleService.createRoom(roomDto);
        redirectAttributes.addFlashAttribute("response", responseDto);
        return "redirect:/schedule/managment/rooms";
    }

}
