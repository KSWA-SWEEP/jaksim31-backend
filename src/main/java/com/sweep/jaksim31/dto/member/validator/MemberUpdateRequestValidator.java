package com.sweep.jaksim31.dto.member.validator;

import com.sweep.jaksim31.dto.member.MemberUpdateRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * packageName :  com.sweep.jaksim31.dto.member.validator
 * fileName : MemberUpdateRequestValidator
 * author :  김주현
 * date : 2023-01-23
 * description :MemberUpdateRequest input value 유효성 검사를 위한 Validator
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-23              김주현             최초 생성
 */
public class MemberUpdateRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(target.getClass() != MemberUpdateRequest.class){
            return;
        }
        MemberUpdateRequest request = MemberUpdateRequest.class.cast(target);
        // TODO 업데이트 시 업데이트가 되지 않은 항목도 보내주는지 확인하고 각각 Validator 설정할지 말지 결정


    }
}
