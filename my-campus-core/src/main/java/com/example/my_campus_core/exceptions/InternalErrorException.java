package com.example.my_campus_core.exceptions;

public class InternalErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InternalErrorException(String message) {
        super(message);
    }
}
