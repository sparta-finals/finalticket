package com.sparta.finalticket.domain.review.service;

import com.sparta.finalticket.domain.review.service.ReviewCacheService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewCacheAspect {

    private final ReviewCacheService reviewCacheService;

    @Pointcut("execution(* com.sparta.finalticket.domain.review.service.ReviewService.getReviewByGameId(..)) && args(gameId, reviewId)")
    public void getReviewPointcut(Long gameId, Long reviewId) {}

    @AfterReturning(value = "getReviewPointcut(gameId, reviewId)", returning = "reviewData")
    public void cacheReviewData(Long gameId, Long reviewId, String reviewData) {
        reviewCacheService.cacheReviewData(reviewId, reviewData);
    }
}
