package com.example.my_campus_core;

import java.time.LocalDate;

import com.example.my_campus_core.models.UserEntity;
import com.example.my_campus_core.models.UserEntity.UserEntityBuilder;

public final class TestDataUtil {
    public static UserEntityBuilder createUserA() {
        return UserEntity.builder().firstName("Test").lastName("Name").email("test@email.com").password("password")
                .birthDate(LocalDate.now()).role("ROLE_ADMIN").status("Active").address(null).courses(null);
    }
}
