package com.example.my_campus_core.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class TimeUtil {
    public static List<LocalDate> getCurrentWeekWorkdays(int weekOffset) {
        return getWeekWorkdays(LocalDate.now(), weekOffset);
    }

    public static List<LocalDate> getWeekWorkdays(LocalDate date, int weekOffset) {
        List<LocalDate> workdays = new ArrayList<>();
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(weekOffset);
        for (int i = 0; i < 5; i++) {
            workdays.add(monday.plusDays(i));
        }
        return workdays;
    }

    public static List<LocalDate> getWeekStartAndEnd(LocalDate date) {
        List<LocalDate> weekStartAndEnd = new ArrayList<>();
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate friday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        weekStartAndEnd.add(monday);
        weekStartAndEnd.add(friday);
        return weekStartAndEnd;
    }
}
