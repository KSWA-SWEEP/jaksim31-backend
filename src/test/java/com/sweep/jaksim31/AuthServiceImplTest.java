package com.sweep.jaksim31;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sweep.jaksim31.service.impl.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class AuthServiceImplTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    MemberServiceImpl memberServiceImpl;

    @Value("${test.token}")
    String token;


//    @Test
//    @DisplayName("회원가입")
//    public void 회원가입() throws Exception {
//        ObjectNode content = objectMapper.createObjectNode();
//        content.put("email", "ttttt1");
//        content.put("password", "ttttt1");
//        content.put("username", "ttttt1");
//
//        this.mockMvc.perform(
//                post("/api/v1/auth/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(content)))
//                .andExpect(status().isOk());
//    }


    @Test
    @DisplayName("로그인")
    void 로그인() throws Exception {

        ObjectNode content = objectMapper.createObjectNode();
        content.put("email", "ttttt");
        content.put("password", "ttttt");

        this.mockMvc.perform(
                        post("/api/v1/auth/login")
//                                .header("Authorization", "Bearer "+token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(content)))
                .andExpect(status().isOk());
    }



}