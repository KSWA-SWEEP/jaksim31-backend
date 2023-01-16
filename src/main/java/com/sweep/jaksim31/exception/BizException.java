package com.sweep.jaksim31.exception;

import lombok.Getter;

/**
 * packageName :  com.sweep.jaksim31.exception
 * fileName : BizException
 * author :  방근호
 * date : 2023-01-13
 * description : 비즈니스 로직 Runtime Exception 정의
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13             방근호            최초 생성
 *
 */

@Getter
public class BizException extends RuntimeException{
    private final BaseExceptionType baseExceptionType;

    public BizException(BaseExceptionType baseExceptionType){
        super(baseExceptionType.getMessage());
        this.baseExceptionType = baseExceptionType;
    }

}
