package com.example.guesthousebookingsystem.services;

public class CustomerServiceUnavailableException extends RuntimeException {
    public CustomerServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}