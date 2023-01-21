//package com.sweep.jaksim31.domain.members;
//
//import com.sweep.jaksim31.util.ExecutionTimeTestExecutionListener;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.test.context.TestExecutionListeners;
//import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataMongoTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ExtendWith(MockitoExtension.class)
//@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, ExecutionTimeTestExecutionListener.class})
//public class MemberRepositoryTimeTest {
//    @Autowired
//    MongoTemplate mongoTemplate;
//    @Autowired
//    MemberRepository memberRepository;
//
//    public static Members getMembers(int num) {
//        return Members.builder()
//                .loginId("test-loginId" + num)
//                .username("test-username" + num)
//                .password("test-password" + num)
//                .register_date(Instant.now().plus(9, ChronoUnit.HOURS))
//                .update_date(Instant.now().plus(9, ChronoUnit.HOURS))
//                .delYn('N')
//                .isSocial(false)
//                .diaryTotal(0)
//                .recentDiaries(new ArrayList<>())
//                .profileImage("test-profileImage" + num)
//                .build();
//    }
//
//    @BeforeAll
//    static void setup(@Autowired MemberRepository memberRepository){
//        for (int i = 0; i < 10000; i++)
//            memberRepository.save(getMembers(i));
//    }
//
//    @Test
//    @DisplayName("전체 데이터 조회")
//    void findAllData() {
//        List<Members> membersList = memberRepository.findAll();
//        assertThat(membersList.get(0).getLoginId()).isEqualTo("test-loginId0");
//    }
//}
