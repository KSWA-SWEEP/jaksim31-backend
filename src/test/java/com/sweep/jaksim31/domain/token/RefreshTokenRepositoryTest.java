package com.sweep.jaksim31.domain.token;

import com.sweep.jaksim31.domain.members.Members;
import com.sweep.jaksim31.util.ExecutionTimeTestExecutionListener;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, ExecutionTimeTestExecutionListener.class})
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class) // 메소드 순서 지정
class RefreshTokenRepositoryTest {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    ArrayList<RefreshToken> refreshTokenList = new ArrayList<>();

    public RefreshToken getRefreshToken(int num) {
        return RefreshToken.builder()
                .loginId("loginId" + num)
                .value("refreshToken" + num)
                .build();
    }


    @Test
    @Order(1)
    @DisplayName("Refresh Token 저장 및 조회 (Create & Read)")
    void save() {

        //given
        for (int i = 0; i < 10; i++)
            refreshTokenList.add(getRefreshToken(i));

        //when
        refreshTokenRepository.saveAll(refreshTokenList);

        //then
        assertEquals(10, refreshTokenRepository.findAll().size());

    }

    @Test
    @Order(2)
    @DisplayName("Refresh Token 조회 (Read) - LoginId")
    void findByLoginId() {
        //when
        RefreshToken findRefreshToken  = refreshTokenRepository.findByLoginId(refreshTokenList.get(0).getLoginId()).orElse(null);

        //then
        assert findRefreshToken != null;
        assertEquals(findRefreshToken.getLoginId(), refreshTokenList.get(0).getLoginId());
        assertEquals(findRefreshToken.getValue(), refreshTokenList.get(0).getValue());

    }

    @Test
    @Order(3)
    @DisplayName("Refresh Token 삭제 (Delete) - LoginId")
    void deleteByLoginId() {
        //given
        String loginId = "loginId1";

        //when
        refreshTokenRepository.deleteByLoginId(loginId);

        //then
        assertEquals(9, refreshTokenRepository.findAll().size());
        assertThat(refreshTokenRepository.findByLoginId(loginId).orElse(null)).isNull();
    }
}