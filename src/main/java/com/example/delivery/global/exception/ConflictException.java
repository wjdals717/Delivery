package com.example.delivery.global.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}