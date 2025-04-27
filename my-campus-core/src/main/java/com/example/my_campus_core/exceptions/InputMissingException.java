package com.example.my_campus_core.exceptions;

public class InputMissingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InputMissingException(String message) {
        super(message);
    }
}
