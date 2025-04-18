package com.example.my_campus_core.controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.service.ScheduleService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ScheduleController {
    private ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("schedule/managment")
    public String getScheduleManagmentPage(@RequestParam(defaultValue = "0") int weekOffset, Model model) {

        // Calculate current week dates
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.plusWeeks(weekOffset)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Create mocked week days
        List<WeekDay> weekDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            weekDays.add(new WeekDay(
                    date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    date));
        }

        // Create mocked events
        List<CalendarEvent> events = Arrays.asList(
                new CalendarEvent("Team Meeting", startOfWeek.plusDays(1).toString(), 9, 10, "primary"),
                new CalendarEvent("Client Call", startOfWeek.plusDays(2).toString(), 11, 12, "success"),
                new CalendarEvent("Lunch", startOfWeek.plusDays(3).toString(), 12, 13, "warning"),
                new CalendarEvent("Review", startOfWeek.plusDays(4).toString(), 14, 15, "info"));

        // Add to model
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("startOfWeek", startOfWeek);
        model.addAttribute("endOfWeek", endOfWeek);
        model.addAttribute("weekDays", weekDays);
        model.addAttribute("events", events);
        return "./scheduleManagment";
    }

    // Simple record classes for data transfer
    public record WeekDay(String dayName, LocalDate date) {
    }

    public record CalendarEvent(String title, String date, int startHour, int endHour, String type) {
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

    @PostMapping("schedule/managment/rooms/add")
    public String createRoom(@ModelAttribute RoomDto roomDto, RedirectAttributes redirectAttributes) {
        ResponseDto responseDto = scheduleService.createRoom(roomDto);
        redirectAttributes.addFlashAttribute("response", responseDto);
        return "redirect:/schedule/managment/rooms";
    }

}
