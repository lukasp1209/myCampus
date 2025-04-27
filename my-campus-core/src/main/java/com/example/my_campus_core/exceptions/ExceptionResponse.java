package com.example.my_campus_core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExceptionResponse {
    private String code;
    private String message;
    private int status;

}
