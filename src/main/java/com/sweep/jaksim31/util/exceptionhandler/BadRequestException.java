package com.sweep.jaksim31.util.exceptionhandler;

public class BadRequestException extends RuntimeException {

    private String errorMessage;

    public BadRequestException(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}