package com.sweep.jaksim31.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRemoveDTO {
    private String email;
    private String password;
}
