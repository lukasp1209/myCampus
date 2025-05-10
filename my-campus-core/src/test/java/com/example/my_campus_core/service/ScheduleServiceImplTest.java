package com.example.my_campus_core.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.my_campus_core.dto.RoomDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.models.Room;
import com.example.my_campus_core.repository.RoomRepository;
import com.example.my_campus_core.service.impl.ScheduleServiceImpl;
import com.example.my_campus_core.util.Mappers;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceImplTest {

@Mock
private RoomRepository roomRepository;

@InjectMocks
private ScheduleServiceImpl scheduleService;

private RoomDto testRoomDto;
private Room testRoom;

@BeforeEach
void setUp() {
testRoomDto = new RoomDto();
testRoomDto.setName("Test Room");

testRoom = new Room();
testRoom.setName("Test Room");
 }

@Test
void createRoom_Success() {
// Arrange
 when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

// Act
ResponseDto response = scheduleService.createRoom(testRoomDto);

 // Assert
 assertNotNull(response);
 assertEquals("success", response.getStatus());
 assertEquals("Room Test Room created successfully", response.getMessage());
 verify(roomRepository, times(1)).save(any(Room.class));
}

@Test
void createRoom_Error() {
 // Arrange
 doThrow(new RuntimeException("Database error")).when(roomRepository).save(any(Room.class));

// Act
 ResponseDto response = scheduleService.createRoom(testRoomDto);

// Assert
assertNotNull(response);
assertEquals("error", response.getStatus());
assertEquals("Error creating room: Database error", response.getMessage());
 verify(roomRepository, times(1)).save(any(Room.class));
}

@Test
void getAllRooms_WithRooms() {
// Arrange
List<Room> rooms = new ArrayList<>();
rooms.add(testRoom);
when(roomRepository.findAll()).thenReturn(rooms);
RoomDto expectedDto = Mappers.roomToRoomDto(testRoom); // direkter Aufruf

 // Act
List<RoomDto> result = scheduleService.getAllRooms();

// Assert
assertNotNull(result);
assertEquals(1, result.size());
assertEquals("Test Room", result.get(0).getName());
 verify(roomRepository, times(1)).findAll();
 verify(roomRepository, times(1)).findAll();
}


 @Test
 void getAllRooms_NoRooms() {
    // Arrange
    when(roomRepository.findAll()).thenReturn(new ArrayList<>());

    // Act
    List<RoomDto> result = scheduleService.getAllRooms();

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(roomRepository, times(1)).findAll();
}

 @Test
void totalNumberRooms() {
// Arrange
List<Room> rooms = new ArrayList<>();
rooms.add(testRoom);
when(roomRepository.findAll()).thenReturn(rooms);

// Act
Integer result = scheduleService.totalNumberRooms();

// Assert
assertNotNull(result);
assertEquals(1, result);
verify(roomRepository, times(1)).findAll();
}

@Test
void getAllRoomsIgnoreRooms_WithRooms() {
    // Arrange
    List<Room> rooms = new ArrayList<>();
    rooms.add(testRoom);
    
    List<Integer> roomIds = new ArrayList<>();
    roomIds.add(2);

    when(roomRepository.findAllByIdNotIn(roomIds)).thenReturn(rooms);

    // Statt mocking → realer statischer Methodenaufruf
    RoomDto expectedDto = Mappers.roomToRoomDto(testRoom);

    // Act
    List<RoomDto> result = scheduleService.getAllRoomsIgnoreRooms(roomIds);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedDto.getId(), result.get(0).getId());
    assertEquals(expectedDto.getName(), result.get(0).getName());

    verify(roomRepository, times(1)).findAllByIdNotIn(roomIds);
}

@Test
void getAllRoomsIgnoreRooms_NoRooms() {
    // Arrange
    List<Integer> roomIds = new ArrayList<>();
    roomIds.add(2);
    when(roomRepository.findAllByIdNotIn(roomIds)).thenReturn(new ArrayList<>());

    // Act
    List<RoomDto> result = scheduleService.getAllRoomsIgnoreRooms(roomIds);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(roomRepository, times(1)).findAllByIdNotIn(roomIds);
}
}


