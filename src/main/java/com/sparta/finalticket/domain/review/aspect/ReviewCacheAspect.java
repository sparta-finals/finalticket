package com.sparta.finalticket.domain.review.aspect;

import com.sparta.finalticket.domain.review.service.ReviewCacheService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewCacheAspect {

    private final ReviewCacheService reviewCacheService;

    // Pointcut 수정: 리뷰 조회 메서드에 대한 포인트컷 설정
    @Pointcut("execution(* com.sparta.finalticket.domain.review.service.ReviewService.getReviewByGameId(..))")
    public void getReviewPointcut() {}

    // Around 어드바이스 수정: 리뷰 조회 메서드 실행 전후로 캐싱 로직을 적용
    @Around("getReviewPointcut()")
    public Object cacheReviewData(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long gameId = (Long) args[0];
        Long reviewId = (Long) args[1];

        // 캐시에서 리뷰 데이터 가져오기
        String cachedReviewData = reviewCacheService.getCachedReviewData(reviewId);

        if (cachedReviewData != null) {
            // 캐시에 데이터가 존재하면 반환
            return cachedReviewData;
        } else {
            // 캐시에 데이터가 없으면 메서드 실행
            String reviewData = (String) joinPoint.proceed();

            // 실행 결과를 캐시에 저장
            reviewCacheService.cacheReviewData(reviewId, reviewData);

            return reviewData;
        }
    }
}
