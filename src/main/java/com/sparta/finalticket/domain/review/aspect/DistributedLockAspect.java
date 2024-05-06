package com.sparta.finalticket.domain.review.aspect;

import com.sparta.finalticket.domain.review.service.RedisReviewService;
import com.sparta.finalticket.global.exception.review.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedisReviewService redisReviewService;
    private final RedissonClient redissonClient;

    @Pointcut("execution(* com.sparta.finalticket.domain.review.service.ReviewService.createReview(..)) " +
            "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.getReviewByGameId(..)) " +
            "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.updateReview(..)) " +
            "|| execution(* com.sparta.finalticket.domain.review.service.ReviewService.deleteReview(..))")
    public void reviewServiceMethods() {
    }

    @Around("reviewServiceMethods()")
    public Object applyDistributedLock(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        Object[] args = joinPoint.getArgs();

        Long gameId = Arrays.stream(args)
                .filter(arg -> arg instanceof Long)
                .map(arg -> (Long) arg)
                .findFirst()
                .orElse(null);

        if (gameId != null) {
            // 낙관적 락 적용
            long expectedVersion = redissonClient.getAtomicLong("reviewVersion:" + gameId).get();
            if (redisReviewService.checkOptimisticLock(gameId, expectedVersion)) {
                try {
                    result = joinPoint.proceed();
                } finally {
                    // 작업이 성공적으로 수행되면 버전 업데이트
                    redissonClient.getAtomicLong("reviewVersion:" + gameId).incrementAndGet();
                }
            } else {
                throw new OptimisticLockException("Optimistic 락이 gameId에 대해 실패했습니다." + gameId);
            }

            // 리뷰 관련 데이터를 Redis에 업데이트
            redisReviewService.setTotalReviewCount(gameId, redisReviewService.getTotalReviewCount(gameId));
            redisReviewService.setAverageReviewScore(gameId, redisReviewService.getAverageReviewScore(gameId));

            return result;
        } else {
            return joinPoint.proceed();
        }
    }
}
