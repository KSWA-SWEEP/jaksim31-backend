package com.sweep.jaksim31.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateDTO {
    private String email;
    private String password;
    private String username;
//    private List<String> authorities;

}
