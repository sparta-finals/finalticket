package com.sparta.finalticket.global.config.redis;

import org.springframework.transaction.annotation.Transactional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

@Component
public class AopForTransaction {

    @Transactional(propagation = Propagation.REQUIRES_NEW) //메소드 호출시 새로운 트랜잭션 시작
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable { //메소드 실행시점
        return joinPoint.proceed();
    }
}
