package com.example.my_campus_core.security;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findFirstByEmail(username);
        // Get the role of the user
        String role = user.getRole();

        // Create a SimpleGrantedAuthority instance for the role
        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        if (user != null) {
            User authUser = new User(user.getEmail(), user.getPassword(), Collections.singleton(authority));
            return authUser;
        } else {
            throw new UsernameNotFoundException("Ivalid email or password");
        }
    }

    public boolean isHisProfile(String email, int userId) {
        UserEntity user = userRepository.findFirstByEmail(email);
        if (user != null && user.getId() == userId) {
            return true;
        } else {
            return false;
        }
    }

}