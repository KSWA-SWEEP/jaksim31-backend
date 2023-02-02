package com.sweep.jaksim31.dto.member.validator;

import com.sweep.jaksim31.dto.member.MemberCheckPasswordRequest;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.enums.MemberExceptionType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * packageName :  com.sweep.jaksim31.dto.member.validator
 * fileName : MemberCheckPasswordRequestValidator
 * author :  김주현
 * date : 2023-01-23
 * description :MemberCheckPasswordRequest input value 유효성 검사를 위한 Validator
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-23              김주현             최초 생성
 */
public class MemberCheckPasswordRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(target.getClass() != MemberCheckPasswordRequest.class){
            return;
        }
        MemberCheckPasswordRequest request = MemberCheckPasswordRequest.class.cast(target);
        if(Objects.isNull(request.getPassword())|| request.getPassword().length() == 0)
            throw new BizException(MemberExceptionType.NOT_FOUND_PASSWORD);

    }
}
