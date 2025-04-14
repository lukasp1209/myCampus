package com.example.my_campus_core.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.UserService;
import com.example.my_campus_core.util.PasswordGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserServiceImpl implements UserService {
    private PasswordGenerator passwordGenerator;
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(PasswordGenerator passwordGenerator, UserRepository userRepository) {
        this.passwordGenerator = passwordGenerator;
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(UserDto userDto) {

        UserEntity existingUser = userRepository.findByEmail(userDto.getEmail()); // Check if a user with the same email
        // already exists
        if (existingUser != null) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        String password = passwordGenerator.generate();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        System.out.println(password);
        UserEntity user = new UserEntity();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setBirthDate(userDto.getBirthDate());
        user.setRole(userDto.getRole());
        user.setPassword(hashedPassword); // Set the generated password

        userRepository.save(user); // Save the user to the database

    }

    @Override
    public List<UserDto> getUsersAsAdmin(int page) {
        int size = 20; // Number of users per page
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> users = userRepository.findAll(pageable);

        List<UserDto> userDtos = users.stream().map(user -> {
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
            userDto.setBirthDate(user.getBirthDate());
            userDto.setRole(user.getRole());
            return userDto;
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    @Override
    public List<UserDto> getUsersByNameAndRole(String name, String role) {
        List<UserEntity> professors = userRepository.findByRoleAndLastNameContainingIgnoreCase(role, name);
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

}
