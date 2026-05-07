package com.carServices.backend.exception.business;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
