package com.example.my_campus_core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_campus_core.models.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    // Custom query methods can be defined here if needed
    // For example, findByName, findByCapacity, etc.

    List<Room> findAllByIdNotIn(List<Integer> ids);

}
