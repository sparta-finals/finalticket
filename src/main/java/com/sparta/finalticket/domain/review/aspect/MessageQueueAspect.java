package com.sparta.finalticket.domain.review.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MessageQueueAspect {

    @Before("execution(* com.sparta.finalticket.domain.review.service.*.*(..))")
    public void beforeReviewServiceMethod() {
        // 리뷰 서비스 메서드 호출 전에 메시지 큐 관련 로깅
        System.out.println("리뷰 서비스 메서드 실행 전에 메시지 큐 관련 로깅");
    }
}

