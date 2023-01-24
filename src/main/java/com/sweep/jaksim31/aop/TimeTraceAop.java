package com.sweep.jaksim31.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * packageName :  com.sweep.jaksim31.aop
 * fileName : TimeTraceAop
 * author :  방근호
 * date : 2023-01-13
 * description : 모든 메소드들에 대한 시간 측정을 위한 aop
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13           방근호             최초 생성
 *
 */


@Component
@Aspect
public class TimeTraceAop {
    @Around("execution(* com.sweep.jaksim31..*.*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
//        System.out.println("START: " + joinPoint.toString());
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println(joinPoint.toString()+ " " + timeMs +
                    "ms");
        }
    }
}