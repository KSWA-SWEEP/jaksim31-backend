package com.sweep.jaksim31.dto.diary.validator;

import com.sweep.jaksim31.dto.diary.DiaryAnalysisRequest;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.DiaryExceptionType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * packageName :  com.sweep.jaksim31.dto.diary.validator
 * fileName : DiaryAnalysisRequestValidator
 * author :  김주현
 * date : 2023-01-21
 * description :DiaryAnalysisRequest input value 유효성 검증을 위한 Validator
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-21           김주현             최초 생성
 */
public class DiaryAnalysisRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(target.getClass() != DiaryAnalysisRequest.class){
            return;
        }

        DiaryAnalysisRequest request = DiaryAnalysisRequest.class.cast(target);

        if(Objects.isNull(request.getSentences()) || request.getSentences().isEmpty())
            throw new BizException(DiaryExceptionType.INPUT_SENTENCES_IS_NULL);
    }
}
