package com.sweep.jaksim31;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sweep.jaksim31.entity.surveys.Surveys;
import com.sweep.jaksim31.service.surveys.SurveyService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class SurveyServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    SurveyService surveyService;

    @Value("${test.token}")
    String token;

    @Test
    @DisplayName("설문 생성")
    public void 설문_생성() throws Exception {
        ObjectNode content = objectMapper.createObjectNode();
        content.put("svyTitle", "test");
        content.put("svyTitle", "test");
        content.put("svyIntro", "test");
        content.put("svyStartDt", "2022-10-28T08:30:09.045Z");
        content.put("svyEndDt", "2022-10-28T08:30:09.045Z");
        content.put("svyEndMsg", "test");
        content.put("svyRespMax", 0);
        content.put("svyRespCount", 0);
        content.put("svyType","basic");

        this.mockMvc.perform(
                post("/api/v1/surveys")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(content)))
                        .andExpect(status().isOk());
    }


    @Test
    public void 연결_확인() throws Exception {
        this.mockMvc.perform(get("/api/v1/auth/test"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("나의 전체 설문 조회")
    public void 설문_조회() throws Exception {
        ObjectNode content = objectMapper.createObjectNode();
        this.mockMvc.perform(
                        get("/api/v1/surveys")
                                .header("Authorization", "Bearer "+token)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(content)))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("설문 업데이트")
    void 설문_업데이트() throws Exception {
        ObjectNode content = objectMapper.createObjectNode();
        content.put("svyTitle", "test");
        content.put("svyIntro", "test");
        content.put("svyStartDt", "2022-10-28T08:30:09.045Z");
        content.put("svyEndDt", "2022-10-28T08:30:09.045Z");
        content.put("svyEndMsg", "test");
        content.put("svyRespMax", 0);

        this.mockMvc.perform(
                        get("/api/v1/surveys/585")
                                .header("Authorization", "Bearer "+token)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(content)))
                )
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Type 별 설문 조회")
    void findByType() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("type", "basic");

        this.mockMvc.perform(
                        get("/api/v1/surveys/type")
                                .header("Authorization", "Bearer "+token)
                                .params(params)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(content)))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("설문 삭제")
    void remove() throws Exception {
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("type", "basic");

        this.mockMvc.perform(
                        delete("/api/v1/surveys/585")
                                .header("Authorization", "Bearer "+token)
//                                .params(params)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(content)))
                )
                .andExpect(status().isOk());
    }
}