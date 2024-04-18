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

    @Pointcut("execution(* com.sparta.finalticket.domain.review.service.ReviewService.getReviewByGameId(..)) && args(gameId, reviewId)")
    public void getReviewPointcut(Long gameId, Long reviewId) {}

    @Around("getReviewPointcut(gameId, reviewId)")
    public Object cacheReviewData(ProceedingJoinPoint joinPoint, Long gameId, Long reviewId) throws Throwable {
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
