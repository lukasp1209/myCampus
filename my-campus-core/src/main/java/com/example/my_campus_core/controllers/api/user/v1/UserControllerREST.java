package com.example.my_campus_core.controllers.api.user.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_campus_core.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/user/v1")
public class UserControllerREST {
    private UserService userService;

    public UserControllerREST(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search/professors")
    public ResponseEntity getProfessors(@RequestParam String search) {
        //
        return new ResponseEntity(userService.getUsersByNameAndRole(search, "ROLE_PROFESOR"), null, 200);
    }

    @GetMapping("/search/students")
    public ResponseEntity getStudents(@RequestParam String search) {
        //
        return new ResponseEntity(userService.getUsersByNameAndRole(search, "ROLE_STUDENT"), null, 200);
    }

    @GetMapping("/info")
    public ResponseEntity getUserInfo(@RequestParam String email) {
        return new ResponseEntity(userService.getUserByEmail(email), null, 200);
    }

}
