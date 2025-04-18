package com.example.my_campus_core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.repository.RoomRepository;
import com.example.my_campus_core.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private RoomRepository roomRepository;

    @Autowired
    public ScheduleServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public ResponseDto createRoom(RoomDto roomDto) {
        ResponseDto responseDto = new ResponseDto();
        Room room = new Room();
        room.setName(roomDto.getName());
        try {
            roomRepository.save(room);
            responseDto.setMessage("Room " + roomDto.getName() + " created successfully");
            responseDto.setStatus("success");
        } catch (Exception e) {
            responseDto.setMessage("Error creating room: " + e.getMessage());
            responseDto.setStatus("error");
        }
        return responseDto;
    }

    @Override
    public List<RoomDto> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        if (rooms.isEmpty()) {
            return List.of();
        }
        return rooms.stream().map(room -> {
            RoomDto roomDto = new RoomDto();
            roomDto.setId(room.getId());
            roomDto.setName(room.getName());
            return roomDto;
        }).toList();
    }

    @Override
    public Integer totalNumberRooms() {
        int totalRooms = roomRepository.findAll().size();
        return totalRooms;
    }

    @Override
    public List<RoomDto> getAllRoomsIgnoreRooms(List<Integer> roomIds) {
        List<Room> rooms = roomRepository.findAllByIdNotIn(roomIds);
        if (rooms.isEmpty()) {
            return List.of();
        }
        return rooms.stream().filter(room -> !roomIds.contains(room.getId())).map(room -> {
            RoomDto roomDto = new RoomDto();
            roomDto.setId(room.getId());
            roomDto.setName(room.getName());
            return roomDto;
        }).toList();
    }

}
