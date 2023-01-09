package com.sweep.jaksim31;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sweep.jaksim31.service.surveys.SurveyService;
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
class SurveyRespServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    SurveyService surveyService;

    @Value("${test.token}")
    String token;

    @Test
    @DisplayName("설문 응답 조회")
    public void 개별_설문_응답_조회() throws Exception {
        this.mockMvc.perform(
                get("/api/v1/surveys/resp/334")
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON))
//                        .content(objectMapper.writeValueAsString(content)))
                        .andExpect(status().isOk());
    }

    @Test
    @DisplayName("설문에 대한 전체 응답 조회")
    public void 전체_응답_조회() throws Exception {
        this.mockMvc.perform(get("/api/v1/surveys/533/resp")
                .header("Authorization", "Bearer "+ token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("설문 발화 분석")
    public void 설문_발화_분석() throws Exception {
        ObjectNode content = objectMapper.createObjectNode();
        content.put("msg", "좋아|기쁨");
        this.mockMvc.perform(
                        post("/api/v1/conv")
                                .header("Authorization", "Bearer "+token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(content)))

                .andExpect(status().isOk());
    }
}