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
class MemberServiceImplTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    MemberServiceImpl memberServiceImpl;

    @Value("${test.token}")
    String token;


    @Test
    @DisplayName("멤버 정보 조회 - 토큰 x")
    public void 내_정보_내놔() throws Exception {
        this.mockMvc.perform(get("/api/v1/auth/aaaa"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("멤버 정보 조회 - 토큰 o")
    void 내_정보_내놔_성공() throws Exception {
//        ObjectNode content = objectMapper.createObjectNode();
//        content.put("svyTitle", "test");
        this.mockMvc.perform(
                        get("/api/v1/members/aaaa")
                                .header("Authorization", "Bearer "+token)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(content)))
                )
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("내 정보 보기")
    void 내_정보_좀_알려주세요() throws Exception {

        this.mockMvc.perform(
                        get("/api/v1/members")
                                .header("Authorization", "Bearer "+token)
//                                .params(params)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(content)))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("내 정보 변경")
    void 내_정보_변경() throws Exception {
        ObjectNode content = objectMapper.createObjectNode();
        content.put("email", "aaaa");
        content.put("password", "aaaa");
        content.put("username", "aaaa");

        this.mockMvc.perform(
                        put("/api/v1/members")
                                .header("Authorization", "Bearer "+token)
//                                .params(params)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(content))
                )
                .andExpect(status().isOk());
    }

}