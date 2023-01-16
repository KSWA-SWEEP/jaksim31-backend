package com.sweep.jaksim31.exception.type;

import com.sweep.jaksim31.exception.BaseExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * packageName :  com.sweep.jaksim31.exception.type
 * fileName : ObjectStorageExceptionType
 * author :  방근호
 * date : 2023-01-13
 * description : KIC 오브젝트 스토리지 API 관련 예외 Type 정의
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13                방근호             최초 생성
 *
 */

@Getter
public enum ObjectStorageExceptionType implements BaseExceptionType {

    NOT_CREATE_DIRECTORY("NOT_FOUND_DIRECTORY","디렉토리 생성을 실패하였습니다.", HttpStatus.BAD_REQUEST),
    NOT_UPLOAD_IMAGE("NOT_UPLOAD_IMAGE","이미지 업로드에 실패하였습니다.", HttpStatus.BAD_REQUEST);



    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    ObjectStorageExceptionType(String errorCode, String message, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

}
