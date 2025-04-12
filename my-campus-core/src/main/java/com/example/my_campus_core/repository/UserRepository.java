package com.example.my_campus_core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_campus_core.models.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByEmail(String email);

    UserEntity findFirstByEmail(String email);

    // Custom query methods can be defined here if needed
    // For example, findByUsername, findByEmail, etc.

}
