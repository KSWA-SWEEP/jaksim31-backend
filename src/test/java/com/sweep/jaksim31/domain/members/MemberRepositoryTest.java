package com.sweep.jaksim31.domain.members;

import com.sweep.jaksim31.dto.member.MemberUpdateRequest;
import com.sweep.jaksim31.util.ExecutionTimeTestExecutionListener;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest // MongoDBTest method
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 하나의 Repository를 공유
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, ExecutionTimeTestExecutionListener.class}) // 메소드 시간 측정
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class) // 메소드 순서 지정
@Transactional
class MemberRepositoryTest {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    MemberRepository memberRepository;

    public Members getMembers(int num) {
        return Members.builder()
                .loginId("test-loginId" + num)
                .username("test-username" + num)
                .password("test-password" + num)
                .register_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .update_date(Instant.now().plus(9, ChronoUnit.HOURS))
                .delYn('N')
                .isSocial(false)
                .diaryTotal(0)
                .recentDiaries(new ArrayList<>())
                .profileImage("test-profileImage" + num)
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("데이터 저장 (Create)")
    void saveTest() {
        //when
        Members members = getMembers(0);
        Members savedMember = memberRepository.save(members);

        //then
        assertThat(savedMember).isSameAs(members);
        assertThat(savedMember.getLoginId()).isEqualTo(members.getLoginId());
        assertThat(savedMember.getIsSocial()).isEqualTo(false);

    }

    @Test
    @Order(2)
    @DisplayName("멤버 조회 (Read) - LoginId")
    void findByIdTest() {

        //given
        Members members1 = getMembers(1);
        Members members2 = getMembers(2);
        memberRepository.save(members1);
        memberRepository.save(members2);

        //when
        Members findMembers1 = memberRepository.findByLoginId("test-loginId1").orElse(null);
        Members findMembers2 = memberRepository.findByLoginId("test-loginId2").orElse(null);

        //then
        assertThat(findMembers1)
                .extracting("password")
                .isEqualTo("test-password1");
        assertThat(findMembers2)
                .extracting("password")
                .isEqualTo("test-password2");
        assertThat(findMembers1)
                .extracting("password")
                .isEqualTo(members1.getPassword());
        assertThat(findMembers2)
                .extracting("password")
                .isEqualTo(members2.getPassword());
    }

    @Test
    @Order(3)
    @DisplayName("멤버 조회 (Read) - ObjectId")
    void findByObjectIdTest() {

        //given
        String members1 = memberRepository.findAll().get(1).getId();
        String members2 = memberRepository.findAll().get(2).getId();

        //when
        Members findMembers1 = memberRepository.findById(members1).orElse(null);
        Members findMembers2 = memberRepository.findById(members2).orElse(null);

        //then
        assertThat(findMembers1)
                .extracting("password")
                .isEqualTo("test-password1");
        assertThat(findMembers2)
                .extracting("password")
                .isEqualTo("test-password2");

    }

    @Test
    @Order(4)
    @DisplayName("데이터 수정 (Update)") // 0번 멤버 닉네임 수정
    void updateTest() {
        //given
        Members findMembers = memberRepository.findByLoginId("test-loginId0").orElse(null);
        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest("geunho", "http://localhost");
        assert findMembers != null;
        findMembers.updateMember(memberUpdateRequest);

        //when
        memberRepository.save(findMembers);
        Members updatedMember = memberRepository.findById(findMembers.getId()).orElse(null);

        //then
        assertThat(updatedMember)
                .extracting("username")
                        .isEqualTo(memberUpdateRequest.getUsername());

        assertThat(updatedMember)
                .extracting("profileImage")
                .isEqualTo(memberUpdateRequest.getProfileImage());

        assertThat(memberRepository.findAll().size())
                .isEqualTo(3);

        for (Members tmp : mongoTemplate.findAll(Members.class, "member")) {
            System.out.println(tmp.toString());
        }
    }

    @Test
    @Order(5)
    @DisplayName("데이터 삭제 (Delete)") // 3번 멤버 삭제
    void deleteTest() {
        //given
        Members targetMember = memberRepository.findAll().get(2);

        //when
        memberRepository.delete(targetMember);

        //then
        assertThat(memberRepository.findAll().size())
                .isEqualTo(2);

        for (Members tmp : mongoTemplate.findAll(Members.class, "member")) {
            System.out.println(tmp.toString());
        }
    }

    @Test
    @Order(5)
    @DisplayName("데이터 존재 여부 확인 (Exist) - ObjectId") // 0번 멤버 확인
    void existsById() {
        //given
        Members members1 = memberRepository.findAll().get(0);

        //when
        boolean isExist = memberRepository.existsById(members1.getId());

        //then
        assertThat(isExist).isTrue();
    }

    @Test
    @Order(6)
    @DisplayName("데이터 존재 여부 확인 (Exist) - ObjectId") // 0번 멤버 확인
    void existsByLoginId() {
        //given
        Members members1 = memberRepository.findAll().get(0);

        //when
        boolean isExist = memberRepository.existsByLoginId(members1.getLoginId());

        //then
        assertThat(isExist).isTrue();
    }
}