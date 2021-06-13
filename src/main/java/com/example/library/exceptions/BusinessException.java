package com.example.library.exceptions;

public class BusinessException extends RuntimeException{
    public BusinessException(String errorMessage) {
        super(errorMessage);
    }
}
