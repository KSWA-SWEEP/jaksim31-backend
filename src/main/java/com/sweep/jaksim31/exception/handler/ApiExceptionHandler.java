package com.sweep.jaksim31.exception.handler;
import com.sweep.jaksim31.enums.MemberExceptionType;
import com.sweep.jaksim31.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * packageName :  com.sweep.jaksim31.exception.handler
 * fileName : ApiExceptionHandler
 * author :  방근호
 * date : 2023-01-13
 * description : api 예외처리 핸들러
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 * 2023-01-16           방근호             에러 코드 반환
 * 2023-01-17           방근호             리다이렉션 조건 추가
 *
 */
@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<?> handleBadRequestException(BizException ex) throws URISyntaxException {
        log.info("Error Message : " + ex.getMessage());

        // REDIRECTION 시
        if (ex.getBaseExceptionType().getHttpStatus().is3xxRedirection()){

            URI redirectUri = new URI(ex.getRedirectLocation());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);

            return new ResponseEntity<>(
                    new RedirectionResponse(ex.getBaseExceptionType().getErrorCode(), ex.getMessage())
                    ,httpHeaders, ex.getBaseExceptionType().getHttpStatus());
        }

        // 일반 에러 처리 시
        return new ResponseEntity<>(
                new ErrorResponse(ex.getBaseExceptionType().getErrorCode(), ex.getMessage()),
                ex.getBaseExceptionType().getHttpStatus()
        );
    }

    //Validator Exception Handler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    protected ResponseEntity<?> constraintViolationException(ConstraintViolationException e) {
        ErrorResponse errorResponse = new ErrorResponse(MemberExceptionType.INVALID_ID.getErrorCode(), MemberExceptionType.INVALID_ID.getMessage());
        return new ResponseEntity<>(errorResponse, MemberExceptionType.INVALID_ID.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse("INTERNAL_SERVER_ERROR", "내부 서버 오류"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}