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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.dto.UserDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.exceptions.NotFoundException;
import com.example.my_campus_core.exceptions.UnsupportedEntityException;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.UserRepository;
import com.example.my_campus_core.service.UserService;
import com.example.my_campus_core.util.Mappers;
import com.example.my_campus_core.util.PasswordGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Implementation of the UserService interface that handles user-related operations.
 * This service manages user registration, profile management, and user status updates.
 */
@Service
public class UserServiceImpl implements UserService {
    private PasswordGenerator passwordGenerator;
    private UserRepository userRepository;
    private Mappers mappers;

    /**
     * Constructs a new UserServiceImpl with required dependencies.
     *
     * @param passwordGenerator Utility for generating passwords
     * @param userRepository Repository for user-related operations
     * @param mappers Utility for mapping between entities and DTOs
     */
    @Autowired
    public UserServiceImpl(PasswordGenerator passwordGenerator, UserRepository userRepository, Mappers mappers) {
        this.passwordGenerator = passwordGenerator;
        this.userRepository = userRepository;
        this.mappers = mappers;
    }

    /**
     * Registers a new user in the system.
     * Generates a random password and hashes it before storing.
     *
     * @param userDto The user data transfer object containing user information
     * @return List of ResponseDto objects containing registration status and generated password
     */
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

    /**
     * Retrieves a paginated list of users for admin purposes.
     *
     * @param page The page number (0-based)
     * @return List of UserDto objects containing user information
     */
    @Override
    public List<UserDto> getUsersAsAdmin(int page) {
        int size = 20; // Number of users per page
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<UserEntity> users = userRepository.findAll(pageable);

        List<UserDto> userDtos = users.stream().map(user -> {
            UserDto userDto = mappers.userEntityToDto(user);
            return userDto;
        }).toList();

        return userDtos;
    }

    /**
     * Calculates the total number of pages for user pagination.
     *
     * @param size The number of items per page
     * @return The total number of pages
     */
    @Override
    public int totalUsers(int size) {
        int totalUsers = (int) userRepository.count(); // Get the total number of users
        int totalPages = (int) Math.ceil((double) totalUsers / size); // Calculate total pages
        return totalPages;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve
     * @return UserDto containing the user information
     * @throws NotFoundException if the user does not exist
     */
    @Override
    public UserDto getUserById(int userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found!"));
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
    }

    /**
     * Retrieves users by name and role, excluding specific user IDs.
     *
     * @param name The name to search for
     * @param role The role to filter by
     * @param ignoreId List of user IDs to exclude from the results
     * @return List of UserDto objects containing user information
     */
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

    /**
     * Changes the status of a user.
     *
     * @param userId The ID of the user to update
     * @param status The new status to set
     * @return ResponseDto indicating the result of the status change
     * @throws NotFoundException if the user does not exist
     */
    @Override
    public ResponseDto changeUserStatus(int userId, String status) {
        System.out.println("UserID " + userId);
        ResponseDto responseDto = new ResponseDto();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found!"));
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

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address to search for
     * @return UserDto containing the user information
     * @throws UnsupportedEntityException if the email format is invalid
     */
    @Override
    public UserDto getUserByEmail(String email) {
        String decodedEmail;
        try {
            decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            System.err.println("Error decoding email: " + e.getMessage());
            throw new UnsupportedEntityException("Invalid email format: " + email);
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

    /**
     * Updates a user's profile information.
     *
     * @param userDto The user data transfer object containing updated information
     * @return ResponseDto indicating the result of the profile update
     * @throws NotFoundException if the user does not exist
     */
    @Override
    public ResponseDto editUserProfile(UserDto userDto) {
        ResponseDto responseDto = new ResponseDto();
        UserEntity user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userDto.getId()));
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