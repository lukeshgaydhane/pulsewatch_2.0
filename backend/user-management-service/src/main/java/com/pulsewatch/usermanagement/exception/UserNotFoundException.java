package com.pulsewatch.usermanagement.exception;
 
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
} 