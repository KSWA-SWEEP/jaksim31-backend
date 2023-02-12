package com.sweep.jaksim31.exception.handler;


import lombok.Data;

@Data
public class Response {

    private final String errorCode;
    private final String errorMessage;



    public Response(String errorCode, String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
