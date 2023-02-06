package com.sweep.jaksim31.config;

import com.sweep.jaksim31.auth.JwtAccessDeniedHandler;
import com.sweep.jaksim31.auth.JwtAuthenticationEntryPoint;
import com.sweep.jaksim31.auth.JwtFilter;
import com.sweep.jaksim31.auth.TokenProvider;
import com.sweep.jaksim31.service.impl.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * packageName :  com.sweep.jaksim31.config
 * fileName : SecurityConfig
 * author :  방근호
 * date : 2023-01-13
 * description : Spring Security에 대한 설정 jwtfilter를 추가하여 모든 요청에 대해 filter를 거칠 수 있음.
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 * 2023-01-30           방근호             memberService 추가 -> 순환참조 에러 수정
 *
 */


@EnableWebSecurity // 기본적인 웹보안을 사용하겠다는 것
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize 사용을 위함
public class SecurityConfig { // WebSecurityConfigurerAdapter 를 확장하면 보안 관련된 설정을 커스터마이징 할 수 있음
    private final TokenProvider tokenProvider;

    private MemberServiceImpl memberService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    public void setMemberService(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    public MemberServiceImpl getMemberService() {
        return this.memberService;
    }

    /*
     * AuthenticationManager를 주입받기 위해서 빈으로 등록한다.
     * */
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/api-docs/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()

                .and()
                .authorizeRequests()
                .antMatchers("/api/**", "/swagger-ui/**", "/management/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin().disable()
                .csrf().disable()
                .headers().disable()
                .httpBasic().disable()
                .rememberMe().disable()
                .logout().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .addFilterBefore(new JwtFilter(tokenProvider, getMemberService()), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler);
        return http.build();
    }
}


