package com.sparta.finalticket.domain.review.aspect;

import com.sparta.finalticket.domain.review.service.RedisCacheService;
import com.sparta.finalticket.domain.review.service.RedisReviewService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedisCacheService redisCacheService;
    private final RedisReviewService redisReviewService;
    private final RedissonClient redissonClient;

    @Pointcut("execution(* com.sparta.finalticket.domain.review.service.ReviewService.*(..))")
    public void reviewServiceMethods() {
    }

    @Around("reviewServiceMethods()")
    public Object applyDistributedLock(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        Object[] args = joinPoint.getArgs();
        Long gameId = null;

        // gameId를 찾는 로직
        for (Object arg : args) {
            if (arg instanceof Long && gameId == null) {
                gameId = (Long) arg;
                break;
            }
        }

        if (gameId != null) {
            RLock lock = redissonClient.getLock("reviewLock:" + gameId);
            try {
                lock.lock();
                result = joinPoint.proceed();
            } finally {
                lock.unlock();
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
