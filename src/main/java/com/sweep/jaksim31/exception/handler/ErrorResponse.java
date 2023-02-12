package com.sweep.jaksim31.exception.handler;

import lombok.Getter;
import lombok.ToString;
import org.aspectj.apache.bcel.Repository;

@Getter
@ToString
public class ErrorResponse extends Response {
    public ErrorResponse(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
