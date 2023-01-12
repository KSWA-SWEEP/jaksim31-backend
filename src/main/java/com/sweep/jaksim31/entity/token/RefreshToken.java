package com.sweep.jaksim31.entity.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
/**
 * packageName :  com.sweep.jaksim31.entity.token
 * fileName : RefreshToken
 * author :  방근호
 * date : 2023-01-09
 * description : Refresh token Entity
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-09           방근호             최초 생성
 * 2023-01-11           김주현             field 수정
 */


@Getter
@NoArgsConstructor
@Document(collection = "refresh-token")
public class RefreshToken {

    @Id
    private String tokenId;
    private String loginId;
    private String value;

    public void updateValue(String token) {
        this.value = token;
    }

    @Builder
    public RefreshToken(String loginId, String value) {
        this.loginId = loginId;
        this.value = value;
    }
}
