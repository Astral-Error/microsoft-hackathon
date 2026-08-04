package com.example.libraryms.exception;

public class BusinessRuleException extends IllegalArgumentException {

    public BusinessRuleException(String message) {
        super(message);
    }
}