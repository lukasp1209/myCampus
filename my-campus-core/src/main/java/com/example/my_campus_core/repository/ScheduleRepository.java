package com.example.my_campus_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_campus_core.models.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

}
