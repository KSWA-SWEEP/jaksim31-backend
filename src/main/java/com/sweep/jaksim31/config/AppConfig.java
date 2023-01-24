package com.sweep.jaksim31.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * packageName :  com.sweep.jaksim31.config
 * fileName : AppCofnig
 * author :  방근호
 * date : 2023-01-13
 * description : 기본적인 App에 대한 설정 정보 및 빈을 생성하는 코드
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */

@Configuration
public class AppConfig {
    /**
     *
     * @return PasswordEncoder - Spring5 부터 인코더 생성 방법이 변경되었다.
     *  다양한 암호화 알고리즘을 변경가능하도록 한것으로 보인다.
     *   처음에는 SecurityConfig 에 빈으로 생성하였으나 SecurityConfig 에서 CustomLoginIdPasswordAuthProvider 를 의존하고
     *   CustomLoginIdPasswordAuthProvider 는 passwordEncoder(SecurityConfig)에 의존하여 순환참조가 일어나
     *   따로 빈을 만드는 AppConfig를 생성하였다.
     *
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Default 사용
    }
}
