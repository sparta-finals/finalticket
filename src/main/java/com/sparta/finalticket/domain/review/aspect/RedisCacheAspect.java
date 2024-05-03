package com.sparta.finalticket.domain.review.aspect;

import com.sparta.finalticket.domain.review.dto.response.ReviewAspectResponseDto;
import com.sparta.finalticket.domain.review.entity.Review;
import com.sparta.finalticket.domain.review.service.RedisCacheService;
import com.sparta.finalticket.domain.review.service.RedisReviewService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCacheAspect {

    private final RedisCacheService redisCacheService;
    private final RedisReviewService redisReviewService;
    private final RedissonClient redissonClient;

    private static final String REVIEW_LOCK_PREFIX = "reviewLock:";

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
        RLock lock = redissonClient.getLock(REVIEW_LOCK_PREFIX + gameId);
        try {
            lock.lock();
            String cachedReviewData = redisCacheService.getCachedReviewData(reviewId);
            if (cachedReviewData != null) {
                return new ReviewAspectResponseDto(cachedReviewData);
            } else {
                Review reviewData = (Review) joinPoint.proceed();
                redisCacheService.cacheReviewData(reviewId, reviewData);
                return new ReviewAspectResponseDto(reviewData);
            }
        } finally {
            lock.unlock();
        }
    }

    private Object handleReviewModification(Long gameId, ProceedingJoinPoint joinPoint) throws Throwable {
        RLock lock = redissonClient.getLock(REVIEW_LOCK_PREFIX + gameId);
        try {
            lock.lock();
            clearCacheAndProceed(joinPoint);
            Object result = joinPoint.proceed();
            refreshCache();
            updateRedisReviewData(gameId);
            return result;
        } finally {
            lock.unlock();
        }
    }

    private void clearCacheAndProceed(ProceedingJoinPoint joinPoint) throws Throwable {
        redisCacheService.clearReviewCache();
        joinPoint.proceed();
    }

    private void refreshCache() {
        redisCacheService.clearAllReviews();
    }

    private void updateRedisReviewData(Long gameId) {
        redisReviewService.setTotalReviewCount(gameId, redisReviewService.getTotalReviewCount(gameId));
        redisReviewService.setAverageReviewScore(gameId, redisReviewService.getAverageReviewScore(gameId));
    }
}