package com.sparta.finalticket.domain.review.aspect;

import com.sparta.finalticket.domain.review.dto.response.ReviewResponseDto;
import com.sparta.finalticket.domain.review.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ReviewCachingAspect {

    private final RedisCacheService redisCacheService;

    @Pointcut("execution(* com.sparta.finalticket.domain.review.service.ReviewService.getReviewByGameId(..)) && args(gameId, reviewId)")
    public void reviewRetrieval(Long gameId, Long reviewId) {}

    @AfterReturning(
        pointcut = "reviewRetrieval(gameId, reviewId)",
        returning = "reviewResponseDto",
        argNames = "gameId,reviewId,reviewResponseDto"
    )
    public void cacheReview(Long gameId, Long reviewId, ReviewResponseDto reviewResponseDto) {
        if (reviewResponseDto != null) {
            redisCacheService.cacheReviewData(reviewId, reviewResponseDto);
        }
    }
}
