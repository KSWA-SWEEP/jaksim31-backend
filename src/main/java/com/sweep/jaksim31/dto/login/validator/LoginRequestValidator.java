package com.sweep.jaksim31.dto.login.validator;

import com.sweep.jaksim31.dto.login.LoginRequest;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.MemberExceptionType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * packageName :  com.sweep.jaksim31.dto.login.validator
 * fileName : LoginRequestValidator
 * author :  김주현
 * date : 2023-01-22
 * description :LoginRequest input value 유효성 검사를 위한 Validator
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-22              김주현             최초 생성
 */
public class LoginRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(target.getClass() != LoginRequest.class){
            return;
        }
        LoginRequest request = LoginRequest.class.cast(target);
        if(Objects.isNull(request.getLoginId()))
            throw new BizException(MemberExceptionType.NOT_FOUND_LOGIN_ID);
        if(Objects.isNull(request.getPassword()))
            throw new BizException(MemberExceptionType.NOT_FOUND_PASSWORD);

    }
}
