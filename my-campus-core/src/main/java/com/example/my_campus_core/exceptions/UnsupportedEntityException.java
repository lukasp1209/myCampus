package com.example.my_campus_core.exceptions;

public class UnsupportedEntityException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedEntityException(String message) {
        super(message);
    }
}
