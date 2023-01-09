package com.sweep.jaksim31.util;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorResponse {

    private String errorMessage;

    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
