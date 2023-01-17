package com.sweep.jaksim31.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class MembersApiControllerTest123 {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Member TEST API 테스트")
    void test1() throws Exception {
        mockMvc.perform(get("/v0/members/test"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print(System.out));
    }

    @Test
    void signup() {
    }

    @Test
    void login() {
    }

    @Test
    void kakaoLogin() {
    }

    @Test
    void isMember() {
    }

    @Test
    void reissue() {
    }

    @Test
    void changePw() {
    }

    @Test
    void getMyInfoByLoginId() {
    }

    @Test
    void getMyInfo() {
    }

    @Test
    void updateMember() {
    }

    @Test
    void remove() {
    }

    @Test
    void isMyPw() {
    }

    @Test
    void logout() {
    }

    @Test
    void kakaoLogout() {
    }
}