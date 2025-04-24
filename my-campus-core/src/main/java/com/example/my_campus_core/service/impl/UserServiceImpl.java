package com.example.my_campus_core.service.impl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.UserService;
import com.example.my_campus_core.util.Mappers;
import com.example.my_campus_core.util.PasswordGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserServiceImpl implements UserService {
    private PasswordGenerator passwordGenerator;
    private UserRepository userRepository;
    private Mappers mappers;

    @Autowired
    public UserServiceImpl(PasswordGenerator passwordGenerator, UserRepository userRepository, Mappers mappers) {
        this.passwordGenerator = passwordGenerator;
        this.userRepository = userRepository;
        this.mappers = mappers;
    }

    @Override
    public List<ResponseDto> registerUser(UserDto userDto) {
        ResponseDto toast = new ResponseDto();
        ResponseDto infoMessage = new ResponseDto();
        List<ResponseDto> responseDtos = new ArrayList<>();
        UserEntity existingUser = userRepository.findByEmail(userDto.getEmail()); // Check if a user with the same email
        // already exists
        if (existingUser != null) {
            toast.setStatus("error");
            toast.setMessage("User with this email already exists.");
            infoMessage.setStatus("error");
            infoMessage.setMessage("User with email " + userDto.getEmail() + " already exists.");
            responseDtos.add(toast);
            responseDtos.add(infoMessage);
            return responseDtos;
        }

        String password = passwordGenerator.generate();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        UserEntity user = new UserEntity();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setBirthDate(userDto.getBirthDate());
        user.setRole(userDto.getRole());
        user.setPassword(hashedPassword); // Set the generated password
        userRepository.save(user); // Save the user to the database
        toast.setStatus("success");
        toast.setMessage("User registered successfully."); // Include the generated password
        infoMessage.setStatus("success");
        infoMessage.setMessage("User registered successfully. Password: " + password);
        responseDtos.add(toast);
        responseDtos.add(infoMessage);
        return responseDtos;
    }

    @Override
    public List<UserDto> getUsersAsAdmin(int page) {
        int size = 20; // Number of users per page
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> users = userRepository.findAll(pageable);

        List<UserDto> userDtos = users.stream().map(user -> {
            UserDto userDto = mappers.userEntityToDto(user);
            return userDto;
        }).toList();

        return userDtos;
    }

    @Override
    public int totalUsers(int size) {
        int totalUsers = (int) userRepository.count(); // Get the total number of users
        int totalPages = (int) Math.ceil((double) totalUsers / size); // Calculate total pages
        return totalPages;
    }

    @Override
    public UserDto getUserById(int userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setEmail(user.getEmail());
            userDto.setAddress(user.getAddress());
            userDto.setStatus(user.getStatus());
            userDto.setBirthDate(user.getBirthDate());
            userDto.setRole(user.getRole());
            return userDto;
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    @Override
    public List<UserDto> getUsersByNameAndRole(String name, String role, List<Integer> ignoreId) {
        List<UserEntity> professors = userRepository.findByRoleAndStatusNotAndLastNameContainingIgnoreCaseAndIdNotIn(
                role,
                "Inactive", name, ignoreId);
        return professors.stream().map(user -> {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setEmail(user.getEmail());
            userDto.setAddress(user.getAddress());
            userDto.setBirthDate(user.getBirthDate());
            userDto.setRole(user.getRole());
            return userDto;
        }).toList();
    }

    @Override
    public ResponseDto changeUserStatus(int userId, String status) {
        System.out.println("UserID " + userId);
        ResponseDto responseDto = new ResponseDto();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        if (!user.getStatus().equals(status)) { // Only update if the status is different
            user.setStatus(status);
            System.out.println("Status changed to: " + user.getStatus());
            userRepository.save(user);
            responseDto.setStatus("success");
            responseDto.setMessage("User with ID " + userId + " status changed to: " + status);
        } else {
            System.out.println("Status is already: " + status);
            responseDto.setStatus("success");
            responseDto.setMessage("User with ID " + userId + " already has status: " + status);

        }

        return responseDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        String decodedEmail;
        try {
            decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            System.err.println("Error decoding email: " + e.getMessage());
            throw new IllegalArgumentException("Invalid email format: " + email, e);
        }
        UserEntity user = userRepository.findByEmail(decodedEmail);
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setAddress(user.getAddress());
        userDto.setBirthDate(user.getBirthDate());
        userDto.setRole(user.getRole());

        return userDto;
    }

    @Override
    public ResponseDto editUserProfile(UserDto userDto) {
        ResponseDto responseDto = new ResponseDto();
        UserEntity user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userDto.getId()));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setAddress(userDto.getAddress());
        System.out.println("Address " + userDto.getAddress());
        userRepository.saveAndFlush(user);
        responseDto.setStatus("success");
        responseDto.setMessage("Profile updated successfully.");
        return responseDto;
    }
}