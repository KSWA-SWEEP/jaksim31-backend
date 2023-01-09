package com.sweep.jaksim31;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class QboxServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    MemberServiceImpl memberServiceImpl;

    @Value("${test.token}")
    String token;


    @Test
    @DisplayName("Q-BOX 조회")
    public void 큐박스_조회() throws Exception {
        this.mockMvc.perform(
                        get("/api/v1/qbox")
                                .header("Authorization", "Bearer "+token)
                                .contentType(MediaType.APPLICATION_JSON))
//                                .content(objectMapper.writeValueAsString(content)))
                .andExpect(status().isOk());
    }

}