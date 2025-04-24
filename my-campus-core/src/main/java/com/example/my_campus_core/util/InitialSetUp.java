package com.example.my_campus_core.util;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.my_campus_core.dto.InitialSetUpDto;
import com.example.my_campus_core.dto.response.ResponseDto;
import com.example.my_campus_core.models.TimeSlot;
import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.repository.TimeSlotRepository;
import com.example.my_campus_core.repository.UserRepository;

@Component
public class InitialSetUp {
    private UserRepository userRepository;
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    public InitialSetUp(UserRepository userRepository, TimeSlotRepository timeSlotRepository) {
        this.userRepository = userRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    public ResponseDto initialSetUp() {
        ResponseDto responseDto = new ResponseDto();
        Long users = userRepository.count();
        if (users != 0) {
            responseDto.setStatus("error");
            responseDto.setMessage(
                    "My Campus platform is already set up.");
            return responseDto;
        }
        responseDto.setStatus("success");
        responseDto.setMessage("Please provide an email and password to register the initial admin user.");

        return responseDto;
    }

    public ResponseDto initialUserAndTimeSlots(InitialSetUpDto initialSetUpDto) {
        UserEntity initialUser = new UserEntity();
        ResponseDto responseDto = new ResponseDto();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(initialSetUpDto.getPassword());
        initialUser.setEmail(initialSetUpDto.getEmail());
        initialUser.setPassword(hashedPassword);
        initialUser.setRole("ROLE_ADMIN");
        userRepository.save(initialUser);

        int slotCounter = 1;
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.getValue() >= DayOfWeek.MONDAY.getValue() &&
                    day.getValue() <= DayOfWeek.FRIDAY.getValue()) {
                for (int i = 0; i < 3; i++) {
                    TimeSlot slot = new TimeSlot();
                    slot.setDayOfWeek(day);
                    slot.setStartTime(LocalTime.of(9, 0).plusHours(i * 3));
                    slot.setEndTime(LocalTime.of(12, 0).plusHours(i * 3));

                    timeSlotRepository.save(slot);
                    slotCounter += 1;
                }
            }
        }

        responseDto.setStatus("success");
        responseDto.setMessage("Initial root admin account successfully setup");
        return responseDto;

    }
}
