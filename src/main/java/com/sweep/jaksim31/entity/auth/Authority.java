package com.sweep.jaksim31.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "authority")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority {
    @Id
    private String id;
    private MemberAuth authorityName;

    public String getAuthorityName() {
        return this.authorityName.toString();
    }
}
