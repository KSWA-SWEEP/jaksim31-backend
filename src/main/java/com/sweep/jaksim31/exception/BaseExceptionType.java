package com.sweep.jaksim31.exception;

import org.springframework.http.HttpStatus;

/**
 * packageName :  com.sweep.jaksim31.exception
 * fileName : BaseExceptionType
 * author :  방근호
 * date : 2023-01-13
 * description : 여러 Exception Type들이 BizException에서 사용될 수 있도록 interface 생성
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13                방근호             최초 생성
 *
 */

public interface BaseExceptionType {
    String getErrorCode();
    String getMessage();
    HttpStatus getHttpStatus();
}
