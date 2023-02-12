package com.sweep.jaksim31.exception.handler;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RedirectionResponse  extends Response{
    public RedirectionResponse(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
