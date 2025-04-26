package com.example.my_campus_core.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_campus_core.models.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    Schedule findByDateFromAndDateTo(LocalDate dateFrom, LocalDate dateTo);

}
