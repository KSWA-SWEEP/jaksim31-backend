package com.sweep.jaksim31.exception.handler;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorResponse {

    private final String errorMessage;
    private final String errorCode;

    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
