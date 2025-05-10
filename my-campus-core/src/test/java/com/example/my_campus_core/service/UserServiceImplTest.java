package com.example.my_campus_core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.exceptions.NotFoundException;
import com.example.my_campus_core.models.Address;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.impl.UserServiceImpl;
import com.example.my_campus_core.util.Mappers;
import com.example.my_campus_core.util.PasswordGenerator;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Mappers mappers;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto testUserDto;
    private UserEntity testUserEntity;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testAddress = new Address();
        testAddress.setId(1);
        testAddress.setCountry("USA");
        testAddress.setCity("New York");
        testAddress.setZip("10001");
        testAddress.setStreet("Test Street");
        testAddress.setHouseNumber("123");

        testUserDto = new UserDto();
        testUserDto.setId(1);
        testUserDto.setFirstName("John");
        testUserDto.setLastName("Doe");
        testUserDto.setEmail("john.doe@example.com");
        testUserDto.setAddress(testAddress);
        testUserDto.setRole("STUDENT");

        testUserEntity = new UserEntity();
        testUserEntity.setId(1);
        testUserEntity.setFirstName("John");
        testUserEntity.setLastName("Doe");
        testUserEntity.setEmail("john.doe@example.com");
        testUserEntity.setAddress(testAddress);
        testUserEntity.setRole("STUDENT");
        testUserEntity.setStatus("Active");
    }

    @Test
    void registerUser_Success() {
        try (MockedStatic<PasswordGenerator> mockedStatic = Mockito.mockStatic(PasswordGenerator.class)) {
            // Arrange
            when(userRepository.findByEmail(testUserDto.getEmail())).thenReturn(null);
            mockedStatic.when(PasswordGenerator::generate).thenReturn("testPassword");
            when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);

            // Act
            List<ResponseDto> response = userService.registerUser(testUserDto);

            // Assert
            assertNotNull(response);
            assertEquals(2, response.size());
            assertEquals("success", response.get(0).getStatus());
            assertEquals("User registered successfully.", response.get(0).getMessage());
            verify(userRepository, times(1)).save(any(UserEntity.class));
        }
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUserEntity));

        // Act
        UserDto result = userService.getUserById(1);

        // Assert
        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        assertEquals(testUserDto.getFirstName(), result.getFirstName());
        assertEquals(testUserDto.getLastName(), result.getLastName());
        assertEquals(testUserDto.getEmail(), result.getEmail());
    }

    @Test
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void changeUserStatus_Success() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);

        // Act
        ResponseDto response = userService.changeUserStatus(1, "Inactive");

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getStatus());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}