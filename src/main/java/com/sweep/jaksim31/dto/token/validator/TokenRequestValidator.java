package com.sweep.jaksim31.dto.token.validator;

import com.sweep.jaksim31.dto.token.TokenRequest;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.JwtExceptionType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * packageName :  com.sweep.jaksim31.dto.token.validator
 * fileName : TokenRequestValidator
 * author :  김주현
 * date : 2023-01-22
 * description :TokenRequest input value 유효성 검사를 위한 Validator
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-22           김주현             최초 생성
 */
public class TokenRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(target.getClass() != TokenRequest.class){
            return;
        }
        TokenRequest request = TokenRequest.class.cast(target);
        // 입력 된 토큰이 아무것도 없을 경우
        if(Objects.isNull(request.getAccessToken()) && Objects.isNull(request.getRefreshToken()))
            throw new BizException(JwtExceptionType.EMPTY_TOKEN);

    }
}
