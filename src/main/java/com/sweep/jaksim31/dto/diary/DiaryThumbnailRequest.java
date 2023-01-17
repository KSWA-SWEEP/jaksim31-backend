package com.sweep.jaksim31.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName :  com.sweep.jaksim31.dto.diary
 * fileName : DiaryThumbnailRequest
 * author :  방근호
 * date : 2023-01-12
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-12                방근호             최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryThumbnailRequest {
    private String userId;
    private String diaryId;
    private String thumbnail;

}
