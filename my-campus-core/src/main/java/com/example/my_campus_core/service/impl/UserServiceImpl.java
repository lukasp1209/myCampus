package com.example.my_campus_core.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
    public void updateUserDetails(String username, String newDetails) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUserDetails'");
    }

    @Override
    public void deleteUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }
    // Implement the methods defined in the UserService interface here

}
