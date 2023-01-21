package com.sweep.jaksim31.dto.diary.validator;

import com.sweep.jaksim31.dto.diary.DiaryThumbnailRequest;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.DiaryExceptionType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * packageName :  com.sweep.jaksim31.dto.diary.validator
 * fileName : DiaryThumbnailRequestValidator
 * author :  김주현
 * date : 2023-01-21
 * description :DiaryThumbnailRequest input value 유효성 검증을 위한 Validator
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-21           김주현             최초 생성
 */
public class DiaryThumbnailRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(target.getClass() != DiaryThumbnailRequest.class){
            return;
        }
        DiaryThumbnailRequest request = DiaryThumbnailRequest.class.cast(target);

        if(Objects.isNull(request.getUserId()))
            throw new BizException(DiaryExceptionType.USER_ID_IS_NULL);
        if(Objects.isNull(request.getDiaryId()))
            throw new BizException(DiaryExceptionType.DIARY_ID_IS_NULL);
        if(Objects.isNull(request.getThumbnail()))
            throw new BizException(DiaryExceptionType.THUMBNAIL_IS_NULL);
    }
}
