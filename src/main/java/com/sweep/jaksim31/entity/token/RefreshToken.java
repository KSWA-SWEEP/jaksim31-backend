package com.sweep.jaksim31.entity.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Getter
@NoArgsConstructor
@Document(collection = "refresh-token")
public class RefreshToken {

    @Id
    private String tokenId;
    private String email;
    private String value;

    public void updateValue(String token) {
        this.value = token;
    }

    @Builder
    public RefreshToken(String email, String value) {
        this.email = email;
        this.value = value;
    }
}
