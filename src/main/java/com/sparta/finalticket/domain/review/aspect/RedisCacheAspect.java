package com.sparta.finalticket.domain.review.aspect;

import com.sparta.finalticket.domain.review.dto.response.ReviewAspectResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCacheAspect {

    private final RedisCacheService redisCacheService;

    @Pointcut("execution(* com.sparta.finalticket.domain.review.service.ReviewService.getReviewByGameId(..)) " +
            "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.createReview(..)) " +
            "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.updateReview(..)) " +
            "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.deleteReview(..))")
    public void reviewPointcut() {}

    @Around("reviewPointcut()")
    public Object cacheReviewDataForReviewService(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long gameId;
        Object result;

        if (joinPoint.getSignature().getName().equals("getReviewByGameId")) {
            gameId = (Long) args[0];
            Long reviewId = (Long) args[1];
            return cacheReviewData(gameId, reviewId, joinPoint);
        } else {
            gameId = (Long) args[0];
            return handleReviewModification(gameId, joinPoint);
        }
    }

    private Object cacheReviewData(Long gameId, Long reviewId, ProceedingJoinPoint joinPoint) throws Throwable {
        String cacheKey = "review:" + gameId + ":" + reviewId;
        Object cachedReviewData = redisCacheService.getCachedData(cacheKey);

        if (cachedReviewData != null) {
            return new ReviewAspectResponseDto(String.valueOf(cachedReviewData));
        } else {
            Review reviewData = (Review) joinPoint.proceed();
            redisCacheService.cacheData(cacheKey, reviewData);
            return new ReviewAspectResponseDto(reviewData);
        }
    }

    private Object handleReviewModification(Long gameId, ProceedingJoinPoint joinPoint) throws Throwable {
        // Clear cache for all reviews
        redisCacheService.clearAllReviews();
        Object result = joinPoint.proceed();
        return result;
    }
}