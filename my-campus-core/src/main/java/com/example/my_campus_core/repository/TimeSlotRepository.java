package com.example.my_campus_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_campus_core.models.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

}
