package com.sweep.jaksim31.util.exceptionhandler;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {
    String getErrorCode();
    String getMessage();
    HttpStatus getHttpStatus();
}
