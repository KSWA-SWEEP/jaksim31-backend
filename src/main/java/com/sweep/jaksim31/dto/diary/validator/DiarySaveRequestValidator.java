package com.sweep.jaksim31.dto.diary.validator;


import com.sweep.jaksim31.dto.diary.DiarySaveRequest;
import com.sweep.jaksim31.exception.BizException;
import com.sweep.jaksim31.exception.type.DiaryExceptionType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Objects;

/**
 * packageName :  com.sweep.jaksim31.dto.diary.validator
 * fileName : DiarySaveRequestValidator
 * author :  김주현
 * date : 2023-01-21
 * description : DiarySaveRequest input value 유효성 검증을 위한 Validator
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-21           김주현             최초 생성
 */
public class DiarySaveRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(target.getClass() != DiarySaveRequest.class){
            return;
        }
        DiarySaveRequest request = DiarySaveRequest.class.cast(target);

        if(Objects.isNull(request.getUserId()) || request.getUserId().length() == 0)
            throw new BizException(DiaryExceptionType.USER_ID_IS_NULL);
        if(Objects.isNull(request.getContent()) || request.getContent().length() == 0)
            throw new BizException(DiaryExceptionType.CONTENT_IS_NULL);
        if(Objects.isNull(request.getDate()))
            throw new BizException(DiaryExceptionType.DIARY_DATE_IS_NULL);
        if(request.getDate().compareTo(ChronoLocalDate.from(LocalDateTime.now())) > 0)
            throw new BizException(DiaryExceptionType.WRONG_DATE);
        if(Objects.isNull(request.getEmotion()) || request.getEmotion().length() == 0)
            throw new BizException(DiaryExceptionType.EMOTION_IS_NULL);
        if(Objects.isNull(request.getKeywords()) || request.getKeywords().length == 0)
            throw new BizException(DiaryExceptionType.KEYWORDS_IS_NULL);
        if(Objects.isNull(request.getThumbnail()) || request.getThumbnail().length() == 0)
            throw new BizException(DiaryExceptionType.THUMBNAIL_IS_NULL);

    }
}
